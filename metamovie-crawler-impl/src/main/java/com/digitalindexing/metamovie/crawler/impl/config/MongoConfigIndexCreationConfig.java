package com.digitalindexing.metamovie.crawler.impl.config;

import com.digitalindexing.metamovie.crawler.impl.entity.InitializationLog;
import com.digitalindexing.metamovie.crawler.impl.entity.MetaMovieEntity;
import java.util.Set;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.util.ClassTypeInformation;

@Configuration
public class MongoConfigIndexCreationConfig {

  @Autowired private ApplicationContext applicationContext;

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired private MongoMappingContext mongoMappingContext;

  @EventListener(ApplicationReadyEvent.class)
  public void initIndicesAfterStartup() {
    Set<Class<?>> entityClasses =
        new Reflections(InitializationLog.class.getPackageName())
            .getTypesAnnotatedWith(MetaMovieEntity.class);
    entityClasses.stream().forEach(this::ensureIndex);
  }

  private void ensureIndex(Class<?> classParam) {
    IndexOperations indexOps = mongoTemplate.indexOps(classParam);
    MongoPersistentEntityIndexResolver resolver =
        new MongoPersistentEntityIndexResolver(mongoMappingContext);
    resolver.resolveIndexFor(ClassTypeInformation.from(classParam)).forEach(indexOps::ensureIndex);
  }
}
