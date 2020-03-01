package com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization.imdb;

import com.digitalindexing.metamovie.crawler.impl.config.MessagingChannelsConfig;
import com.digitalindexing.metamovie.crawler.impl.entity.InitializationStatus;
import com.digitalindexing.metamovie.crawler.impl.service.InitializationLogService;
import com.digitalindexing.metamovie.crawler.impl.util.InitializationLogIdWrapper;
import com.digitalindexing.metamovie.datastore.api.model.Movie;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
@MessageEndpoint
@EnableBinding(IMDBInitializationKafkaSink.class)
public class IMDBInitializationSendToDatastoreKafkaEndpoint {

  @Autowired private InitializationLogService initializationLogService;

  @Autowired private IMDBInitializationKafkaSink initializationKafkaSink;

  @ServiceActivator(
    inputChannel = MessagingChannelsConfig.IMDB_MOVIE_DATASTORE_IN,
    outputChannel = MessagingChannelsConfig.IMDB_MOVIE_DATASTORE_OUT,
    poller =
        @Poller(
          fixedRate = "500",
          maxMessagesPerPoll = "4",
          errorChannel = MessagingChannelsConfig.CUSTOM_ERROR_CHANNEL,
          taskExecutor = MessagingChannelsConfig.DATA_STORAGE_EXECUTOR_NAME
        )
  )
  public Optional<Movie> handleMessage(
      Optional<Movie> movieElement,
      @Header(MessagingChannelsConfig.IMDB_BASIC_IX_HEADER) Long ix,
      @Header(MessagingChannelsConfig.INITIALIZATION_LOG_ID_HEADER)
          InitializationLogIdWrapper initializationLogIdWrapper) {
    if (!movieElement.isEmpty()) {
      Movie movie = movieElement.get();
      log.debug(
          "Sending movie to kafka pipeline for metamovie datastorage with imdbID '{}'",
          movie.getImdbId());

      try {
        Map<String, Object> map =
            Map.of(
                MessagingChannelsConfig.IMDB_BASIC_IX_HEADER,
                ix,
                MessagingChannelsConfig.INITIALIZATION_LOG_ID_HEADER,
                initializationLogIdWrapper);
        if (!initializationKafkaSink
            .output()
            .send(MessageBuilder.createMessage(movie, new MessageHeaders(map)))) {
          updateInitializationLogStatus(
              initializationLogIdWrapper.getInitializationLogId(), InitializationStatus.FAILED);
          throw new IllegalStateException(
              "Error sending message to kafka pipeline for movie data initialization");
        } else {
          updateInitializationLogStatus(
              initializationLogIdWrapper.getInitializationLogId(),
              InitializationStatus.MOVIE_COMPLETED);
        }
      } catch (Exception e) {
        log.error(
            "Error storing movie data with imdb '{}' to datastore for index '{}'",
            movie.getImdbId(),
            ix);
        throw e;
      }
    }

    return movieElement;
  }

  private void updateInitializationLogStatus(
      String initializationLogId, InitializationStatus status) {
    initializationLogService.updateInitializationLogStatus(initializationLogId, status);
  }
}
