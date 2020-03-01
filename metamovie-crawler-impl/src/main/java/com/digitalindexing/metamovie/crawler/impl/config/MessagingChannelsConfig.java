package com.digitalindexing.metamovie.crawler.impl.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class MessagingChannelsConfig {

  public static final String IMDB_BASIC_IX_HEADER = "countHeader";

  /* EXECUTORS */

  public static final String MOVIE_DB_DETAILS_EXECUTOR_NAME = "movieDBDetailsExecutorName";
  public static final String MOVIE_DB_TRANSLATIONS_EXECUTOR_NAME =
      "movieDBTranslationsExecutorName";
  public static final String DATA_STORAGE_EXECUTOR_NAME = "datastorageExecutorName";
  public static final String INITIALIZATION_LOG_ID_HEADER = "initializationLog";
  public static final String MOVIE_POSTERS_EXECUTOR_NAME = "moviePostersExecutorName";

  @Bean(name = MOVIE_DB_DETAILS_EXECUTOR_NAME)
  public TaskExecutor movieDBDetailsExecutorName() {
    return createTaskExecutor();
  }

  @Bean(name = MOVIE_DB_TRANSLATIONS_EXECUTOR_NAME)
  public TaskExecutor movieDBTranslationsExecutorName() {
    return createTaskExecutor();
  }

  @Bean(name = DATA_STORAGE_EXECUTOR_NAME)
  public TaskExecutor datastorageExecutorName() {
    return createTaskExecutor();
  }

  @Bean(name = MOVIE_POSTERS_EXECUTOR_NAME)
  public TaskExecutor moviePostersExecutorName() {
    return createTaskExecutor();
  }

  private TaskExecutor createTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(8);
    taskExecutor.setMaxPoolSize(12);
    taskExecutor.setQueueCapacity(100000);
    taskExecutor.initialize();
    return taskExecutor;
  }

  /* CHANNELS */

  public static final String IMDB_CHANNEL_1 = "imdbChannel1";
  public static final String IMDB_CHANNEL_2 = "imdbChannel2";
  public static final String IMDB_CHANNEL_4 = "imdbChannel4";
  public static final String IMDB_CHANNEL_5 = "imdbChannel5";
  public static final String IMDB_CHANNEL_6 = "imdbChannel6";
  public static final String IMDB_CHANNEL_7 = "imdbChannel7";
  public static final String IMDB_CHANNEL_8 = "imdbChannel8";
  public static final String IMDB_CHANNEL_9 = "imdbChannel9";
  public static final String CUSTOM_ERROR_CHANNEL = "customErrorChannel";

  @Bean(name = IMDB_CHANNEL_1)
  public MessageChannel imdbChannel1() {
    return new ExecutorChannel(eventDrivenPipesExecutor());
  }

  @Bean(name = IMDB_CHANNEL_2)
  public MessageChannel imdbChannel2() {
    return new ExecutorChannel(eventDrivenPipesExecutor());
  }

  @Bean(name = IMDB_CHANNEL_4)
  public MessageChannel imdbChannel4() {
    return new ExecutorChannel(eventDrivenPipesExecutor());
  }

  @Bean(name = IMDB_CHANNEL_5)
  public MessageChannel imdbChannel5() {
    return new ExecutorChannel(eventDrivenPipesExecutor());
  }

  @Bean(name = IMDB_CHANNEL_6)
  public MessageChannel imdbChannel6() {
    return new QueueChannel(1000);
  }

  @Bean(name = IMDB_CHANNEL_7)
  public MessageChannel imdbChannel7() {
    return new QueueChannel(1000);
  }

  @Bean(name = IMDB_CHANNEL_8)
  public MessageChannel imdbChannel8() {
    return new QueueChannel(10000);
  }

  @Bean(name = IMDB_CHANNEL_9)
  public MessageChannel imdbChannel9() {
    return new QueueChannel(100000);
  }

  @Bean(name = CUSTOM_ERROR_CHANNEL)
  public MessageChannel customErrorChannel() {
    return new PublishSubscribeChannel();
  }

  private ExecutorService eventDrivenPipesExecutor() {
    return Executors.newFixedThreadPool(8);
  }

  /* ENDPOINT ORDER */

  public static final String IMDB_GATEWAY_ENTRY_POINT = IMDB_CHANNEL_1;
  public static final String IMDB_MAP_TO_ARRAY_IN = IMDB_CHANNEL_1;
  public static final String IMDB_MAP_TO_ARRAY_OUT = IMDB_CHANNEL_2;
  public static final String IMDB_FILTER_CATEGORY_IN = IMDB_CHANNEL_2;
  public static final String IMDB_FILTER_CATEGORY_OUT = IMDB_CHANNEL_4;
  public static final String IMDB_MAP_TO_DTO_IN = IMDB_CHANNEL_4;
  public static final String IMDB_MAP_TO_DTO_OUT = IMDB_CHANNEL_5;
  public static final String IMDB_FILTER_PROCESSED_IN = IMDB_CHANNEL_5;
  public static final String IMDB_FILTER_PROCESSED_OUT = IMDB_CHANNEL_6;
  public static final String IMDB_MOVIE_DB_1_IN = IMDB_CHANNEL_6;
  public static final String IMDB_MOVIE_DB_1_OUT = IMDB_CHANNEL_7;
  public static final String IMDB_MOVIE_DB_2_IN = IMDB_CHANNEL_7;
  public static final String IMDB_MOVIE_DB_2_OUT = IMDB_CHANNEL_8;
  public static final String IMDB_MOVIE_DATASTORE_IN = IMDB_CHANNEL_8;
  public static final String IMDB_MOVIE_DATASTORE_OUT = IMDB_CHANNEL_9;
  public static final String IMDB_MOVIE_DB_POSTERS_IN = IMDB_CHANNEL_9;
}
