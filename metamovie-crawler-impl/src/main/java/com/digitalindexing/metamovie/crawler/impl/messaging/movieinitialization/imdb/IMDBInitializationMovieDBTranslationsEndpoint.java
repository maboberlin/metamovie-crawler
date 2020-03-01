package com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization.imdb;

import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.api.TheMovieDBFeignClient;
import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.CountryData;
import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.MovieTranslations;
import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.Translation;
import com.digitalindexing.metamovie.crawler.impl.config.MessagingChannelsConfig;
import com.digitalindexing.metamovie.datastore.api.model.Movie;
import com.digitalindexing.metamovie.datastore.api.model.aux.TranslationData;
import feign.FeignException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@MessageEndpoint
public class IMDBInitializationMovieDBTranslationsEndpoint {

  @Value("#{'${metamovie.translations.iso639}'.split(',')}")
  private List<String> validTranslations;

  @Autowired private TheMovieDBFeignClient feignClient;

  @Transformer(
    inputChannel = MessagingChannelsConfig.IMDB_MOVIE_DB_2_IN,
    outputChannel = MessagingChannelsConfig.IMDB_MOVIE_DB_2_OUT,
    poller =
        @Poller(
          fixedRate = "500",
          maxMessagesPerPoll = "4",
          errorChannel = MessagingChannelsConfig.CUSTOM_ERROR_CHANNEL,
          taskExecutor = MessagingChannelsConfig.MOVIE_DB_TRANSLATIONS_EXECUTOR_NAME
        )
  )
  public Optional<Movie> handleMessage(
      Optional<Movie> movieElement, @Header(MessagingChannelsConfig.IMDB_BASIC_IX_HEADER) Long ix) {
    if (!movieElement.isEmpty()) {
      Movie movie = movieElement.get();
      log.debug("Requesting movie translations from movie db for imdb '{}'", movie.getImdbId());

      try {
        MovieTranslations translations = feignClient.getMovieTranslations(movie.getImdbId());

        Set<Translation> validTranslations = filterValidTranslationPosters(translations);

        enrichData(movie, validTranslations);
      } catch (FeignException ex) {
        if (ex.status() == 404) {
          log.debug("Movie with imdb '{}' not found", movie.getImdbId());
          return Optional.empty();
        }
        throw ex;
      }
    }
    return movieElement;
  }

  private Set<Translation> filterValidTranslationPosters(MovieTranslations translations) {
    return Optional.ofNullable(translations)
        .map(MovieTranslations::getTranslations)
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .filter(translation -> validTranslations.contains(translation.getIso6391()))
        .collect(Collectors.groupingBy(el -> el.getIso6391()))
        .values()
        .stream()
        .map(
            el ->
                Optional.ofNullable(el)
                    .orElse(Collections.emptyList())
                    .stream()
                    .findFirst()
                    .orElse(null))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  private void enrichData(Movie movie, Set<Translation> translations) {
    List<TranslationData> translationsDataList =
        translations
            .stream()
            .map(
                translation ->
                    new TranslationData(
                        translation.getIso6391(),
                        translation.getEnglishName(),
                        Optional.ofNullable(translation.getCountryData())
                            .map(CountryData::getTitle)
                            .orElse(null),
                        Optional.ofNullable(translation.getCountryData())
                            .map(CountryData::getOverview)
                            .orElse(null),
                        null,
                        null,
                        null))
            .collect(Collectors.toList());
    movie.setTranslationDataList(translationsDataList);
  }
}
