package com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization.imdb;

import com.digitalindexing.metamovie.crawler.impl.config.MessagingChannelsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;

@Slf4j
@MessageEndpoint
public class IMDBInitializationMapToArrayEndpoint {

  @Transformer(
    inputChannel = MessagingChannelsConfig.IMDB_MAP_TO_ARRAY_IN,
    outputChannel = MessagingChannelsConfig.IMDB_MAP_TO_ARRAY_OUT
  )
  public String[] handleMessage(String dataLine) {
    return dataLine.split("\\s*\\t\\s*");
  }
}
