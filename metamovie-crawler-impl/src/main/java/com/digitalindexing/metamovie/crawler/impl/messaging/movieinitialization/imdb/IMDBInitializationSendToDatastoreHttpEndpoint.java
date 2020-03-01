package com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization.imdb;

import com.digitalindexing.metamovie.crawler.impl.config.MessagingChannelsConfig;
import com.digitalindexing.metamovie.crawler.impl.entity.InitializationStatus;
import com.digitalindexing.metamovie.crawler.impl.service.InitializationLogService;
import com.digitalindexing.metamovie.crawler.impl.util.InitializationLogIdWrapper;
import com.digitalindexing.metamovie.datastore.api.client.MovieControllerClient;
import com.digitalindexing.metamovie.datastore.api.model.Movie;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
// @MessageEndpoint
public class IMDBInitializationSendToDatastoreHttpEndpoint {

  @Autowired private MovieControllerClient movieControllerClient;

  @Autowired private InitializationLogService initializationLogService;

  @ServiceActivator(
    inputChannel = MessagingChannelsConfig.IMDB_MOVIE_DATASTORE_IN,
    requiresReply = "false",
    poller =
        @Poller(
          fixedRate = "500",
          maxMessagesPerPoll = "4",
          errorChannel = MessagingChannelsConfig.CUSTOM_ERROR_CHANNEL,
          taskExecutor = MessagingChannelsConfig.DATA_STORAGE_EXECUTOR_NAME
        )
  )
  public void handleMessage(
      Optional<Movie> movieElement,
      @Header(MessagingChannelsConfig.IMDB_BASIC_IX_HEADER) Long ix,
      @Header(MessagingChannelsConfig.INITIALIZATION_LOG_ID_HEADER)
          InitializationLogIdWrapper initializationLogIdWrapper) {
    if (!movieElement.isEmpty()) {
      Movie movie = movieElement.get();
      log.debug("Storing movie to metamovie datastorage for imdb '{}'", movie.getImdbId());

      try {
        Movie result = movieControllerClient.createMovie(movie);

        updateInitializationLogStatusCompleted(initializationLogIdWrapper.getInitializationLogId());
      } catch (Exception e) {
        log.error(
            "Error storing movie data with imdb '{}' to datastore for index '{}'",
            movie.getImdbId(),
            ix);
        throw e;
      }
    }
  }

  private void updateInitializationLogStatusCompleted(String initializationLogId) {
    initializationLogService.updateInitializationLogStatus(
        initializationLogId, InitializationStatus.MOVIE_COMPLETED);
  }
}
