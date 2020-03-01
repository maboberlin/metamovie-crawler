package com.digitalindexing.metamovie.crawler.impl.messaging.movieinitialization.imdb;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface IMDBInitializationKafkaSink {
  String OUTPUT = "imdb-initialization-kafka";

  @Output(IMDBInitializationKafkaSink.OUTPUT)
  MessageChannel output();
}
