package com.digitalindexing.metamovie.crawler.impl.client.themoviedb.api;

import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.config.TheMovieDBClientConfiguration;
import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.MovieDetails;
import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.MovieImages;
import com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto.MovieTranslations;
import javax.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
  value = "themoviedbclient",
  url = "${metamovie.themoviedb.api.host}",
  path = "${metamovie.themoviedb.api.path}",
  configuration = TheMovieDBClientConfiguration.class
)
public interface TheMovieDBFeignClient {

  @RequestMapping(value = "/movie/{imdbID}", method = RequestMethod.GET)
  MovieDetails getMovieDetails(@NotEmpty @PathVariable("imdbID") String imdbID);

  @RequestMapping(value = "/movie/{imdbID}/translations", method = RequestMethod.GET)
  MovieTranslations getMovieTranslations(@NotEmpty @PathVariable("imdbID") String imdbID);

  @RequestMapping(value = "/movie/{imdbID}/images", method = RequestMethod.GET)
  MovieImages getMovieImages(@NotEmpty @PathVariable("imdbID") String imdbID);
}
