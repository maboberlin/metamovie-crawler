
package com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "backdrops", "posters"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieImages {

  @JsonProperty("id")
  public Integer id;

  @JsonProperty("backdrops")
  public List<Backdrop> backdrops = null;

  @JsonProperty("posters")
  public List<Poster> posters = null;
}
