package com.e205.repository;

import com.e205.entity.LostItemNotification;
import com.e205.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostItemNotificationRepository extends
    JpaRepository<LostItemNotification, Integer> {

  List<Notification> findByLostItemId(Integer lostItemId);
}
