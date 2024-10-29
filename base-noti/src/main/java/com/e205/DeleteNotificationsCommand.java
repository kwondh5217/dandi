package com.e205;

import java.util.List;

public record DeleteNotificationsCommand(List<Integer> notificationIds) {

}
