package com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization.imdb;

import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.api.TheMovieDBFeignClient;
import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.MovieImages;
import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.Poster;
import com.digitalindexing.metamovie.crawler.impl.config.MessagingChannelsConfig;
import com.digitalindexing.metamovie.crawler.impl.entity.InitializationStatus;
import com.digitalindexing.metamovie.crawler.impl.service.InitializationLogService;
import com.digitalindexing.metamovie.crawler.impl.util.InitializationLogIdWrapper;
import com.digitalindexing.metamovie.datastore.api.messaging.PosterDataMessageElement;
import com.digitalindexing.metamovie.datastore.api.model.Movie;
import com.digitalindexing.metamovie.datastore.api.model.aux.PosterData;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
@MessageEndpoint
@EnableBinding(IMDBPostersKafkaSink.class)
public class IMDBInitializationHandlePostersKafkaEndpoint {

  @Value("#{'${metamovie.translations.iso639}'.split(',')}")
  private List<String> validTranslations;

  @Value("${metamovie.themoviedb.image.url}")
  private String metamovieImageUrl;

  @Autowired private TheMovieDBFeignClient feignClient;

  @Autowired private InitializationLogService initializationLogService;

  @Autowired private IMDBPostersKafkaSink postersKafkaSink;

  @ServiceActivator(
    inputChannel = MessagingChannelsConfig.IMDB_MOVIE_DB_POSTERS_IN,
    requiresReply = "false",
    poller =
        @Poller(
          fixedRate = "500",
          maxMessagesPerPoll = "4",
          errorChannel = MessagingChannelsConfig.CUSTOM_ERROR_CHANNEL,
          taskExecutor = MessagingChannelsConfig.MOVIE_POSTERS_EXECUTOR_NAME
        )
  )
  public void handleMessage(
      Optional<Movie> movieElement,
      @Header(MessagingChannelsConfig.IMDB_BASIC_IX_HEADER) Long ix,
      @Header(MessagingChannelsConfig.INITIALIZATION_LOG_ID_HEADER)
          InitializationLogIdWrapper initializationLogIdWrapper) {
    if (!movieElement.isEmpty()) {
      Movie movie = movieElement.get();
      log.debug(
          "Sending poster data to kafka pipeline for metamovie datastorage with imdbID '{}'",
          movie.getImdbId());

      MovieImages images = feignClient.getMovieImages(movie.getImdbId());

      Set<Poster> posterSet = filterValidTranslationPosters(images);

      Set<PosterDataMessageElement> posterDataMessageElementSet =
          posterSet
              .stream()
              .map(poster -> buildPosterDataElement(movie.getImdbId(), poster))
              .filter(Objects::nonNull)
              .collect(Collectors.toSet());

      posterDataMessageElementSet
          .stream()
          .forEach(
              messageElement ->
                  sendPosterDataToKafka(messageElement, ix, initializationLogIdWrapper));
    }
  }

  private Set<Poster> filterValidTranslationPosters(MovieImages images) {
    return Optional.ofNullable(images)
        .map(MovieImages::getPosters)
        .orElse(Collections.emptyList())
        .stream()
        .filter(Objects::nonNull)
        .filter(poster -> validTranslations.contains(poster.getIso6391()))
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

  private PosterDataMessageElement buildPosterDataElement(String movieImdbId, Poster poster) {
    String posterURL = String.format("%s%s", metamovieImageUrl, poster.getFilePath());
    try (InputStream inputStream = (new URL(posterURL)).openStream()) {
      byte[] content = Base64.getEncoder().encode(IOUtils.toByteArray(inputStream));
      PosterData posterData =
          new PosterData(null, posterURL, poster.getHeight(), poster.getWidth());
      return new PosterDataMessageElement(movieImdbId, poster.getIso6391(), content, posterData);
    } catch (Exception e) {
      log.error("Error fetching poster image for movie with imdbID '{}'", movieImdbId, e);
      return null;
    }
  }

  private void sendPosterDataToKafka(
      PosterDataMessageElement posterDataMessageElement,
      Long ix,
      InitializationLogIdWrapper initLogIdWrapper) {
    try {
      Map<String, Object> map =
          Map.of(
              MessagingChannelsConfig.IMDB_BASIC_IX_HEADER,
              ix,
              MessagingChannelsConfig.INITIALIZATION_LOG_ID_HEADER,
              initLogIdWrapper);
      Message<PosterDataMessageElement> message =
          MessageBuilder.createMessage(posterDataMessageElement, new MessageHeaders(map));

      boolean sendSuccessfully = postersKafkaSink.output().send(message);

      if (!sendSuccessfully) {
        updateInitializationLogStatus(
            initLogIdWrapper.getInitializationLogId(), InitializationStatus.POSTER_FAILED);
        throw new IllegalStateException(
            "Error sending message to kafka pipeline for poster data initialization");
      } else {
        log.debug(
            "Sent poster data to kafka pipeline for metamovie datastorage with imdbID '{}' and translation '{}'",
            posterDataMessageElement.getImdbId(),
            posterDataMessageElement.getLanguageIso639());
        updateInitializationLogStatus(
            initLogIdWrapper.getInitializationLogId(), InitializationStatus.POSTER_COMPLETED);
      }
    } catch (Exception e) {
      log.error(
          "Error storing poster data for movie with imdb '{}' to datastore for index '{}'",
          posterDataMessageElement.getImdbId(),
          ix);
      throw e;
    }
  }

  private void updateInitializationLogStatus(
      String initializationLogId, InitializationStatus status) {
    initializationLogService.updateInitializationLogStatus(initializationLogId, status);
  }
}
