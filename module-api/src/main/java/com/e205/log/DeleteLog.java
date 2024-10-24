package com.e205.log;


public record DeleteLog<T extends LoggableEntity>(T delete) {

}
