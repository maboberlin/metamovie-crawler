
package com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "poster_path", "backdrop_path"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BelongsToCollection {

  @JsonProperty("id")
  public Integer id;

  @JsonProperty("name")
  public String name;

  @JsonProperty("poster_path")
  public String posterPath;

  @JsonProperty("backdrop_path")
  public String backdropPath;
}
