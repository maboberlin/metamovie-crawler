package com.digitalindexing.metamovie.crawler.impl.repository;

import com.digitalindexing.metamovie.crawler.impl.entity.InitializationLog;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InitializationLogRepository
    extends MongoRepository<InitializationLog, String>, CustomInitializationLogRepository {
  List<InitializationLog> findByImdbId(String imdbId, Sort sort);
}
