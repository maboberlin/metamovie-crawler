
package com.digitalindexing.metamovie.crawler.impl.client.themoviedb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "logo_path", "name", "origin_country"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductionCompany {
  @JsonProperty("id")
  public Integer id;

  @JsonProperty("logo_path")
  public String logoPath;

  @JsonProperty("name")
  public String name;

  @JsonProperty("origin_country")
  public String originCountry;
}
