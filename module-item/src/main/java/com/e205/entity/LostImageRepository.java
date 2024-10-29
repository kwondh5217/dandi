package com.e205.entity;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostImageRepository extends JpaRepository<LostImage, UUID> {

}