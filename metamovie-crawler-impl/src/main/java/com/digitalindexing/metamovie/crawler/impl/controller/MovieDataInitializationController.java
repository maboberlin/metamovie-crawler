package com.digitalindexing.metamovie.crawler.impl.controller;

import com.digitalindexing.metamovie.crawler.impl.controller.dto.FilenameDto;
import com.digitalindexing.metamovie.crawler.impl.service.MovieDataInitializationService;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class MovieDataInitializationController {

  @Autowired private MovieDataInitializationService movieDataInitializationService;

  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(value = "/initialize/_imdb_basic", method = RequestMethod.POST)
  public void initializeImdbBasic(@NotNull @Valid @RequestBody FilenameDto filenameDto) {
    movieDataInitializationService.initializeImdbBasic(filenameDto.getFileName());
  }
}
