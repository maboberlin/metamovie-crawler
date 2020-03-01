
package com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "aspect_ratio",
  "file_path",
  "height",
  "iso_639_1",
  "vote_average",
  "vote_count",
  "width"
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Poster {

  @JsonProperty("aspect_ratio")
  public Double aspectRatio;

  @JsonProperty("file_path")
  public String filePath;

  @JsonProperty("height")
  public Integer height;

  @JsonProperty("iso_639_1")
  public String iso6391;

  @JsonProperty("vote_average")
  public Double voteAverage;

  @JsonProperty("vote_count")
  public Integer voteCount;

  @JsonProperty("width")
  public Integer width;
}
