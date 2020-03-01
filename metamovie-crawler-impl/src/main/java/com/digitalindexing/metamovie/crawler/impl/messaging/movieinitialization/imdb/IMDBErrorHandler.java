package com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization.imdb;

import com.digitalindexing.metamovie.crawler.impl.config.MessagingChannelsConfig;
import com.digitalindexing.metamovie.crawler.impl.service.InitializationLogService;
import com.digitalindexing.metamovie.crawler.impl.util.InitializationLogIdWrapper;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.support.ErrorMessage;

@Slf4j
@MessageEndpoint
public class IMDBErrorHandler {

  @Autowired private InitializationLogService initializationLogService;

  @ServiceActivator(inputChannel = MessagingChannelsConfig.CUSTOM_ERROR_CHANNEL)
  public void handleMessage(ErrorMessage errorMessage) {
    String msg =
        Optional.ofNullable(errorMessage.getPayload().getCause())
            .map(Throwable::getMessage)
            .orElse("");
    log.error("Error occurred in messaging system: {}", msg, errorMessage.getPayload());

    String initializationLogId =
        Optional.ofNullable(errorMessage.getOriginalMessage().getHeaders())
            .map(headers -> headers.get(MessagingChannelsConfig.INITIALIZATION_LOG_ID_HEADER))
            .filter(obj -> InitializationLogIdWrapper.class.isAssignableFrom(obj.getClass()))
            .map(obj -> (InitializationLogIdWrapper) obj)
            .map(InitializationLogIdWrapper::getInitializationLogId)
            .orElse(null);

    if (initializationLogId == null
        || !initializationLogService.updateInitializationLogStatusToFailed(
            initializationLogId, msg)) {
      log.error("Error updating initialization log status for element");
    }
  }
}
