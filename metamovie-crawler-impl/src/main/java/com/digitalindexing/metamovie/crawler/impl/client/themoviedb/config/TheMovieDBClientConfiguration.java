package com.digitalindexing.metamovie.crawler.impl.client.themoviedb.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TheMovieDBClientConfiguration {

  @Value("${metamovie.themoviedb.api.key}")
  private String apiKey;

  public static final String API_KEY = "api_key";

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.BASIC;
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new ErrorDecoder.Default();
  }

  @Bean
  public OkHttpClient client() {
    return new OkHttpClient();
  }

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      requestTemplate.query(API_KEY, apiKey);
    };
  }
}
