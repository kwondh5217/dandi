package com.e205.querys;

public interface QueryDispatcher<R> {

  R dispatch(Query query);

}
