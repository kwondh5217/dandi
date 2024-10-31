package com.e205.repository;

import com.e205.entity.FoundItemNotification;
import com.e205.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoundItemNotificationRepository extends
    JpaRepository<FoundItemNotification, Integer> {

  List<Notification> findByFoundItemId(Integer foundItemId);

}
