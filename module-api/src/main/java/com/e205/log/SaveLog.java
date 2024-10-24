package com.e205.log;


public record SaveLog<T extends LoggableEntity>(T save) {

}
