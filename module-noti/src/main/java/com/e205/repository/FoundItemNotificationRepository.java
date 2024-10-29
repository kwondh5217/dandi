package com.e205.repository;

import com.e205.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoundItemNotificationRepository extends
    JpaRepository<Notification, Integer> {

}
