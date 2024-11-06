package com.e205.repository;

import com.e205.entity.LostImage;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostImageRepository extends JpaRepository<LostImage, UUID> {

}