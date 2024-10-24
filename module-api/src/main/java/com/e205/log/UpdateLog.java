package com.e205.log;

public record UpdateLog<T extends LoggableEntity>(T before, T after) {

}
