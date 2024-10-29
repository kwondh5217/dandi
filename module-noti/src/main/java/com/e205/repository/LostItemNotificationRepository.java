package com.e205.repository;

import com.e205.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostItemNotificationRepository extends
    JpaRepository<Notification, Integer> {

}
