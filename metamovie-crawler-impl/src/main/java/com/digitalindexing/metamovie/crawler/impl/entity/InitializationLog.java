package com.digitalindexing.metamovie.crawler.impl.entity;

import static com.digitalindexing.metamovie.crawler.impl.entity.InitializationLog.INITIALIZATION_LOG_COLLECTION;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MetaMovieEntity
@Document(collection = INITIALIZATION_LOG_COLLECTION)
public class InitializationLog {

  @Transient public static final String INITIALIZATION_LOG_COLLECTION = "initialization_log";

  @Transient public static final String CREATED_FIELD = "createdDate";
  @Transient public static final String UPDATED_FIELD = "udpatedDate";
  @Transient public static final String STATUS_FIELD = "status";
  @Transient public static final String IMDB_ID_FIELD = "imdbId";
  @Transient public static final String MESSAGING_ID_FIELD = "messagingID";
  @Transient public static final String MESSAGE_FIELD = "message";

  @EqualsAndHashCode.Include @JsonIgnore @Id private String id;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Field(CREATED_FIELD)
  private Date timestamp = new Date();

  @Field(STATUS_FIELD)
  private InitializationStatus status;

  @Field(MESSAGING_ID_FIELD)
  private String messagingID;

  @Field(IMDB_ID_FIELD)
  private String imdbId;

  @Field(MESSAGE_FIELD)
  private String message;
}
