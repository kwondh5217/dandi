package com.e205.querys;

public interface QueryHandler<T extends Query, R> {

  R handle(Query query);
}
