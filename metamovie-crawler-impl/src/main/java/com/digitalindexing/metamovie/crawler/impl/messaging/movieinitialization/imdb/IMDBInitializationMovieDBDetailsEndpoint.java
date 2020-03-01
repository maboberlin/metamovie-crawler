package com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization.imdb;

import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.api.TheMovieDBFeignClient;
import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.BelongsToCollection;
import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.MovieDetails;
import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.ProductionCompany;
import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.ProductionCountry;
import com.digitalindexing.metamovie.crawler.impl.config.MessagingChannelsConfig;
import com.digitalindexing.metamovie.crawler.impl.entity.InitializationLog;
import com.digitalindexing.metamovie.crawler.impl.entity.InitializationStatus;
import com.digitalindexing.metamovie.crawler.impl.service.InitializationLogService;
import com.digitalindexing.metamovie.crawler.impl.util.InitializationLogIdWrapper;
import com.digitalindexing.metamovie.datastore.api.model.Movie;
import com.digitalindexing.metamovie.datastore.api.model.aux.*;
import com.digitalindexing.metamovie.datastore.api.model.aux.Collection;
import feign.FeignException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@MessageEndpoint
public class IMDBInitializationMovieDBDetailsEndpoint {

  private static final FastDateFormat RELEASE_DATE_FORMATTER =
      FastDateFormat.getInstance("yyyy-MM-dd");

  @Autowired private TheMovieDBFeignClient feignClient;

  @Autowired private InitializationLogService initializationLogService;

  @ServiceActivator(
    inputChannel = MessagingChannelsConfig.IMDB_MOVIE_DB_1_IN,
    outputChannel = MessagingChannelsConfig.IMDB_MOVIE_DB_1_OUT,
    poller =
        @Poller(
          fixedRate = "500",
          maxMessagesPerPoll = "4",
          errorChannel = MessagingChannelsConfig.CUSTOM_ERROR_CHANNEL,
          taskExecutor = MessagingChannelsConfig.MOVIE_DB_DETAILS_EXECUTOR_NAME
        )
  )
  public Optional<Movie> handleMessage(
      Optional<Movie> movieElement,
      @Header(MessageHeaders.ID) String messageId,
      @Header(MessagingChannelsConfig.IMDB_BASIC_IX_HEADER) Long ix,
      @Header(MessagingChannelsConfig.INITIALIZATION_LOG_ID_HEADER)
          InitializationLogIdWrapper initializationLogIdWrapper) {
    if (!movieElement.isEmpty()) {
      Movie movie = movieElement.get();
      log.debug("Requesting movie details from movie db for imdb '{}'", movie.getImdbId());

      saveInitializationLog(messageId, movie.getImdbId(), initializationLogIdWrapper);

      try {
        MovieDetails movieDetails = feignClient.getMovieDetails(movie.getImdbId());

        enrichData(movie, movieDetails);
      } catch (FeignException ex) {
        if (ex.status() == 404) {
          log.debug("Movie with imdb '{}' not found", movie.getImdbId());
          updateInitializationLogStatusNotFound(
              initializationLogIdWrapper.getInitializationLogId());
          return Optional.empty();
        }
        throw ex;
      }
    }
    return movieElement;
  }

  private void saveInitializationLog(
      String messageId, String imdbId, InitializationLogIdWrapper initializationLogId) {
    InitializationLog initializationLog = new InitializationLog();
    initializationLog.setImdbId(imdbId);
    initializationLog.setMessagingID(messageId);
    initializationLog.setStatus(InitializationStatus.STARTED);

    initializationLog = initializationLogService.saveInitializationLog(initializationLog);

    initializationLogId.setInitializationLogId(initializationLog.getId());
  }

  private void updateInitializationLogStatusNotFound(String initializationLogId) {
    initializationLogService.updateInitializationLogStatus(
        initializationLogId, InitializationStatus.NOT_FOUND);
  }

  private void enrichData(Movie movie, MovieDetails movieDetails) {
    movie.setAdult(movieDetails.getAdult());
    movie.setRuntimeMinutes(movieDetails.getRuntime());
    movie.setOriginalTitle(movieDetails.getOriginalTitle());
    movie.setOriginalLanguage(movieDetails.getOriginalLanguage());
    movie.setTitle(movieDetails.getTitle());
    movie.setDescription(movieDetails.getOverview());
    movie.setGenreList(buildGenres(movieDetails.getGenres()));
    movie.setReleaseDate(parseReleaseDate(movieDetails.getReleaseDate()));
    movie.setBudget(movieDetails.getBudget().longValue());
    movie.setRevenue(movieDetails.getRevenue().longValue());
    movie.setHomepage(movieDetails.getHomepage());
    movie.setProductionCompanies(buildCompanies(movieDetails.getProductionCompanies()));
    movie.setProductionCountries(buildProductionCountries(movieDetails.getProductionCountries()));
    movie.setStatus(Status.forString(movieDetails.getStatus()));
    movie.setCollection(buildCollection(movieDetails.getBelongsToCollection()));

    Rating theMovieDBPopularity =
        new Rating(movieDetails.getVoteAverage(), RatingPlatform.THE_MOVIE_DB);
    movie.setRatingList(Arrays.asList(theMovieDBPopularity));
    movie.setTheMovieDbPopularity(movieDetails.getPopularity());
  }

  private LocalDate parseReleaseDate(String releaseDate) {
    if (StringUtils.isNotEmpty(releaseDate)) {
      try {
        Date date = RELEASE_DATE_FORMATTER.parse(releaseDate);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      } catch (ParseException e) {
        log.error("Error parsing the movie db release date string ''", releaseDate);
      }
    }
    return null;
  }

  private List<Genre> buildGenres(
      List<com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.Genre> genres) {
    return Optional.ofNullable(genres)
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .map(genre -> new Genre(genre.getId(), genre.getName()))
        .collect(Collectors.toList());
  }

  private List<Country> buildProductionCountries(List<ProductionCountry> productionCountries) {
    return Optional.ofNullable(productionCountries)
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .map(country -> new Country(country.getIso31661(), country.getName()))
        .collect(Collectors.toList());
  }

  private List<Company> buildCompanies(List<ProductionCompany> productionCompanies) {
    return Optional.ofNullable(productionCompanies)
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .map(company -> new Company(company.getId(), company.getOriginCountry(), company.getName()))
        .collect(Collectors.toList());
  }

  private Collection buildCollection(BelongsToCollection collection) {
    return collection == null ? null : new Collection(collection.getId(), collection.getName());
  }
}
