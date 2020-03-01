package com.digitalindexing.metamovie.crawler.impl.service;

import com.digitalindexing.metamovie.crawler.impl.entity.InitializationLog;
import com.digitalindexing.metamovie.crawler.impl.entity.InitializationStatus;

public interface InitializationLogService {
  InitializationLog saveInitializationLog(InitializationLog log);

  boolean updateInitializationLogStatus(String id, InitializationStatus status);

  boolean updateInitializationLogStatusToFailed(String initializationLogId, String msg);
}
