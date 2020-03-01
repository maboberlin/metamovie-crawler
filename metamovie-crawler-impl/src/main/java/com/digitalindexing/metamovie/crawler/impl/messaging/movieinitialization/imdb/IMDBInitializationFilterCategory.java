package com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization.imdb;

import com.digitalindexing.metamovie.crawler.impl.config.MessagingChannelsConfig;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@MessageEndpoint
public class IMDBInitializationFilterCategory {

  private static final List<String> WHITELIST_CATEGORIES =
      Arrays.asList("movie", "tvEpisode", "tvMovie");

  @Filter(
    inputChannel = MessagingChannelsConfig.IMDB_FILTER_CATEGORY_IN,
    outputChannel = MessagingChannelsConfig.IMDB_FILTER_CATEGORY_OUT,
    discardChannel = "nullChannel"
  )
  public boolean handleMessage(
      String[] msg, @Header(MessagingChannelsConfig.IMDB_BASIC_IX_HEADER) Long ix) {
    String type = msg[1];

    if (ix != 1 && WHITELIST_CATEGORIES.contains(type)) {
      return true;
    } else {
      log.debug("filtered imdb data line ix '{}' with imdbId '{}'", ix, msg[0]);
      return false;
    }
  }
}
