
package com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"iso_3166_1", "iso_639_1", "name", "english_name", "data"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Translation {

  @JsonProperty("iso_3166_1")
  public String iso31661;

  @JsonProperty("iso_639_1")
  public String iso6391;

  @JsonProperty("name")
  public String name;

  @JsonProperty("english_name")
  public String englishName;

  @JsonProperty("data")
  public CountryData countryData;
}
