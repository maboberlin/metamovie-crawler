package com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization.imdb;

import com.digitalindexing.metamovie.crawler.impl.config.MessagingChannelsConfig;
import com.digitalindexing.metamovie.crawler.impl.entity.InitializationLog;
import com.digitalindexing.metamovie.crawler.impl.entity.InitializationStatus;
import com.digitalindexing.metamovie.crawler.impl.repository.InitializationLogRepository;
import com.digitalindexing.metamovie.datastore.api.model.Movie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Router;
import org.springframework.messaging.handler.annotation.Header;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@MessageEndpoint
public class IMDBInitializationFilterAlreadyProcessed {

  private static final String NULL_CHANNEL = "nullChannel";

  @Autowired private InitializationLogRepository initializationLogRepository;

  @Router(inputChannel = MessagingChannelsConfig.IMDB_FILTER_PROCESSED_IN)
  public Collection<String> handleMessage(
      Optional<Movie> movieOptional,
      @Header(MessagingChannelsConfig.IMDB_BASIC_IX_HEADER) Long ix) {
    if (movieOptional.isEmpty()) {
      return Collections.singletonList(NULL_CHANNEL);
    }

    String imdbId = movieOptional.get().getImdbId();
    Sort sort = new Sort(Sort.Direction.DESC, InitializationLog.CREATED_FIELD);
    Optional<InitializationLog> initializationLog =
        initializationLogRepository.findByImdbId(imdbId, sort).stream().findFirst();

    if (initializationLog.isPresent()
        && (initializationLog.get().getStatus() == InitializationStatus.MOVIE_COMPLETED
            || initializationLog.get().getStatus() == InitializationStatus.POSTER_COMPLETED
            || initializationLog.get().getStatus() == InitializationStatus.NOT_FOUND)) {
      log.debug("Entry with imdbID '{}' already successfully processed", imdbId);
      return Collections.singletonList(NULL_CHANNEL);
    } else if (initializationLog.isPresent()
        && initializationLog.get().getStatus() == InitializationStatus.POSTER_FAILED) {
      log.debug("Repeating poster fetch step for entry with imdbID '{}'", imdbId);
      return Collections.singletonList(MessagingChannelsConfig.IMDB_MOVIE_DATASTORE_OUT);
    } else {
      log.debug("Continue processing entry with imdbID '{}'", imdbId);
      return Collections.singletonList(MessagingChannelsConfig.IMDB_FILTER_PROCESSED_OUT);
    }
  }
}
