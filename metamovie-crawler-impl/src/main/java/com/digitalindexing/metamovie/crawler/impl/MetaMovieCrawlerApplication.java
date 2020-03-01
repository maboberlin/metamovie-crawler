package com.digitalindexing.metamovie.crawler.impl;

import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.api.TheMovieDBFeignClient;
import com.digitalindexing.metamovie.datastore.api.client.MovieControllerClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

@EnableIntegration
@IntegrationComponentScan
@EnableFeignClients(basePackageClasses = {MovieControllerClient.class, TheMovieDBFeignClient.class})
@EnableMongoRepositories
@SpringBootApplication
public class MetaMovieCrawlerApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication.run(MetaMovieCrawlerApplication.class, args);
  }
}
