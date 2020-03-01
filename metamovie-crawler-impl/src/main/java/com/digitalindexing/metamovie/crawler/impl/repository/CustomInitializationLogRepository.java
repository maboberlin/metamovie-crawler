package com.digitalindexing.metamovie.crawler.impl.repository;

import com.digitalindexing.metamovie.crawler.impl.entity.InitializationStatus;

public interface CustomInitializationLogRepository {
  boolean patchUpdateInitializationLogStatus(String documentId, InitializationStatus status);

  boolean patchUpdateInitializationLogStatusToFailed(String documentId, String msg);
}
