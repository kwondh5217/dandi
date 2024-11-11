package com.e205.repository;

import com.e205.entity.CommentNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentNotificationRepository extends
    JpaRepository<CommentNotification, Integer> {

}
