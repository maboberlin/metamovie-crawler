package com.digitalindexing.metamovie.crawler.impl.repository.impl;

import com.digitalindexing.metamovie.crawler.impl.entity.InitializationLog;
import com.digitalindexing.metamovie.crawler.impl.entity.InitializationStatus;
import com.digitalindexing.metamovie.crawler.impl.repository.CustomInitializationLogRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Slf4j
public class CustomInitializationLogRepositoryImpl implements CustomInitializationLogRepository {

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired private MongoOperations mongoOperations;

  @Override
  public boolean patchUpdateInitializationLogStatus(
      String documentId, InitializationStatus status) {
    Query query = new Query(Criteria.where("_id").is(documentId));
    Update update = new Update().set(InitializationLog.STATUS_FIELD, status);
    UpdateResult result =
        mongoOperations.updateFirst(query, update, InitializationLog.INITIALIZATION_LOG_COLLECTION);
    return result.getModifiedCount() > 0;
  }

  @Override
  public boolean patchUpdateInitializationLogStatusToFailed(String documentId, String msg) {
    Query query = new Query(Criteria.where("_id").is(documentId));
    Update update =
        new Update()
            .set(InitializationLog.STATUS_FIELD, InitializationStatus.FAILED)
            .set(InitializationLog.MESSAGE_FIELD, msg);
    UpdateResult result =
        mongoOperations.updateFirst(query, update, InitializationLog.INITIALIZATION_LOG_COLLECTION);
    return result.getModifiedCount() > 0;
  }
}
