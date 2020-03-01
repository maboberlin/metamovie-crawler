package com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization;

import com.digitalindexing.metamovie.crawler.impl.config.MessagingChannelsConfig;
import com.digitalindexing.metamovie.crawler.impl.util.InitializationLogIdWrapper;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(errorChannel = MessagingChannelsConfig.CUSTOM_ERROR_CHANNEL)
public interface InitializeMovieDataGateway {

  @Gateway(requestChannel = MessagingChannelsConfig.IMDB_GATEWAY_ENTRY_POINT)
  void processImdbBasicLine(
      String dataLine,
      @Header(MessageHeaders.ERROR_CHANNEL) String errorChannel,
      @Header(MessagingChannelsConfig.IMDB_BASIC_IX_HEADER) Long ixHeader,
      @Header(MessagingChannelsConfig.INITIALIZATION_LOG_ID_HEADER)
          InitializationLogIdWrapper initializationLogId);
}
