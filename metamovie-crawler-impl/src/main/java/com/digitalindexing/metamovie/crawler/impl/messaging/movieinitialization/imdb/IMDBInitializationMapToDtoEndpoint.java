package com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization.imdb;

import com.digitalindexing.metamovie.crawler.impl.config.MessagingChannelsConfig;
import com.digitalindexing.metamovie.datastore.api.model.Movie;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@MessageEndpoint
public class IMDBInitializationMapToDtoEndpoint {

  @Transformer(
    inputChannel = MessagingChannelsConfig.IMDB_MAP_TO_DTO_IN,
    outputChannel = MessagingChannelsConfig.IMDB_MAP_TO_DTO_OUT
  )
  public Optional<Movie> handleMessage(
      String[] dataLine, @Header(MessagingChannelsConfig.IMDB_BASIC_IX_HEADER) Long ix) {
    try {
      String imdbId = dataLine[0];
      String title = dataLine[2];
      String originalTitle = dataLine[3];
      Boolean isAdult = dataLine[4].equalsIgnoreCase("1");
      Integer runtimeMinutes = parseInteger(7, dataLine);

      Movie movie = buildMovie(imdbId, title, originalTitle, isAdult, runtimeMinutes);

      return Optional.of(movie);
    } catch (Exception e) {
      log.error("Error parsing imdb data line of index '{}': {}", ix, dataLine, e);
      return Optional.empty();
    }
  }

  private Movie buildMovie(
      String imdbId, String title, String originalTitle, Boolean isAdult, Integer runtimeMinutes) {
    Movie movie = new Movie();
    movie.setImdbId(imdbId);
    movie.setTitle(title);
    movie.setOriginalTitle(originalTitle);
    movie.setAdult(isAdult);
    movie.setRuntimeMinutes(runtimeMinutes);
    return movie;
  }

  private int parseInteger(int i, String[] dataLine) {
    try {
      return Integer.parseInt(dataLine[i]);
    } catch (NumberFormatException e) {
      return -1;
    }
  }
}
