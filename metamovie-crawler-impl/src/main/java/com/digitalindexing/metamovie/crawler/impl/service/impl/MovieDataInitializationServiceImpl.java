package com.digitalindexing.metamovie.crawler.impl.service.impl;

import com.digitalindexing.metamovie.crawler.impl.config.MessagingChannelsConfig;
import com.digitalindexing.metamovie.crawler.impl.exception.FileReadException;
import com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization.InitializeMovieDataGateway;
import com.digitalindexing.metamovie.crawler.impl.service.MovieDataInitializationService;
import com.digitalindexing.metamovie.crawler.impl.util.InitializationLogIdWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MovieDataInitializationServiceImpl implements MovieDataInitializationService {

  @Autowired private InitializeMovieDataGateway initializeMovieDataGateway;

  @Override
  public void initializeImdbBasic(String fileName) {
    try {
      Path dataFilePath = Paths.get(fileName);
      BufferedReader reader = Files.newBufferedReader(dataFilePath);
      long cnt = 0;
      String line;
      while ((line = reader.readLine()) != null) {
        if (cnt % 1000 == 0) {
          log.info("Sending ix '{}'", cnt);
        }
        initializeMovieDataGateway.processImdbBasicLine(
            line,
            MessagingChannelsConfig.CUSTOM_ERROR_CHANNEL,
            ++cnt,
            new InitializationLogIdWrapper());
      }
    } catch (IOException e) {
      String msg = String.format("Error reading imdb basic data file from location '%s'", fileName);
      log.error(msg, fileName);
      throw new FileReadException(msg, e);
    }
  }
}
