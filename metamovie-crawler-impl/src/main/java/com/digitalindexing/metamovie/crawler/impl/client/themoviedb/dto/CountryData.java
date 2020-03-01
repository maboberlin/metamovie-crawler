
package com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"homepage", "overview", "runtime", "tagline", "title"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountryData {

  @JsonProperty("homepage")
  public String homepage;

  @JsonProperty("overview")
  public String overview;

  @JsonProperty("runtime")
  public Integer runtime;

  @JsonProperty("tagline")
  public String tagline;

  @JsonProperty("title")
  public String title;
}
