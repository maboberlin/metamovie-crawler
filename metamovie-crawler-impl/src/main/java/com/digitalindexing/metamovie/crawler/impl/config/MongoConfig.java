package com.digitalindexing.metamovie.crawler.impl.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

  @Value("${metamovie.datastore.mongodb.database}")
  private String databaseName;

  @Value("${metamovie.datastore.mongodb.authentication-database}")
  private String authDatabaseName;

  @Value("${metamovie.datastore.mongodb.username}")
  private String username;

  @Value("${metamovie.datastore.mongodb.password}")
  private String password;

  @Value("${metamovie.datastore.mongodb.host}")
  private String host;

  @Value("${metamovie.datastore.mongodb.port}")
  private Integer port;

  @Override
  protected String getDatabaseName() {
    return databaseName;
  }

  @Override
  public MongoClient mongoClient() {
    return new MongoClient(
        new ServerAddress(host, port),
        MongoCredential.createCredential(username, authDatabaseName, password.toCharArray()),
        MongoClientOptions.builder().build());
  }
}
