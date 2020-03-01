package com.digitalindexing.metamovie.crawler.impl.service.impl;

import com.digitalindexing.metamovie.crawler.impl.entity.InitializationLog;
import com.digitalindexing.metamovie.crawler.impl.entity.InitializationStatus;
import com.digitalindexing.metamovie.crawler.impl.repository.InitializationLogRepository;
import com.digitalindexing.metamovie.crawler.impl.service.InitializationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InitializationLogServiceImpl implements InitializationLogService {

  @Autowired private InitializationLogRepository repository;

  @Override
  public InitializationLog saveInitializationLog(InitializationLog log) {
    return repository.save(log);
  }

  @Override
  public boolean updateInitializationLogStatus(String id, InitializationStatus status) {
    return repository.patchUpdateInitializationLogStatus(id, status);
  }

  @Override
  public boolean updateInitializationLogStatusToFailed(String initializationLogId, String msg) {
    return repository.patchUpdateInitializationLogStatusToFailed(initializationLogId, msg);
  }
}
