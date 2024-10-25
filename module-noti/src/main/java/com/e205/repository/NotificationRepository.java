package com.e205.repository;

import com.e205.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
  Page<Notification> findByMemberId(int memberId, Pageable pageable);
}
