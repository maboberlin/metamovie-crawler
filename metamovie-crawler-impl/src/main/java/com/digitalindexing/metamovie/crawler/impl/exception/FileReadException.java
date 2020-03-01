package com.digitalindexing.metamovie.crawler.impl.exception;

public class FileReadException extends RuntimeException {
  public FileReadException(String s, Throwable t) {
    super(s, t);
  }
}
