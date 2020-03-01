
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
@JsonPropertyOrder({
  "adult",
  "backdrop_path",
  "belongs_to_collection",
  "budget",
  "genres",
  "homepage",
  "id",
  "imdb_id",
  "original_language",
  "original_title",
  "overview",
  "popularity",
  "poster_path",
  "production_companies",
  "production_countries",
  "release_date",
  "revenue",
  "runtime",
  "spoken_languages",
  "status",
  "tagline",
  "title",
  "video",
  "vote_average",
  "vote_count"
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetails {

  @JsonProperty("adult")
  public Boolean adult;

  @JsonProperty("backdrop_path")
  public String backdropPath;

  @JsonProperty("belongs_to_collection")
  public BelongsToCollection belongsToCollection;

  @JsonProperty("budget")
  public Integer budget;

  @JsonProperty("genres")
  public List<Genre> genres = null;

  @JsonProperty("homepage")
  public String homepage;

  @JsonProperty("id")
  public Integer id;

  @JsonProperty("imdb_id")
  public String imdbId;

  @JsonProperty("original_language")
  public String originalLanguage;

  @JsonProperty("original_title")
  public String originalTitle;

  @JsonProperty("overview")
  public String overview;

  @JsonProperty("popularity")
  public Double popularity;

  @JsonProperty("poster_path")
  public String posterPath;

  @JsonProperty("production_companies")
  public List<ProductionCompany> productionCompanies = null;

  @JsonProperty("production_countries")
  public List<ProductionCountry> productionCountries = null;

  @JsonProperty("release_date")
  public String releaseDate;

  @JsonProperty("revenue")
  public Integer revenue;

  @JsonProperty("runtime")
  public Integer runtime;

  @JsonProperty("spoken_languages")
  public List<SpokenLanguage> spokenLanguages = null;

  @JsonProperty("status")
  public String status;

  @JsonProperty("tagline")
  public String tagline;

  @JsonProperty("title")
  public String title;

  @JsonProperty("video")
  public Boolean video;

  @JsonProperty("vote_average")
  public Double voteAverage;

  @JsonProperty("vote_count")
  public Integer voteCount;
}
