package com.e205.cdc;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BinlogPositionRepository extends JpaRepository<BinlogPosition, Integer> {

  int BINLOG_POSITION_ID = 1;

  default Optional<BinlogPosition> findDefaultBinlogPosition() {
    return this.findById(BINLOG_POSITION_ID);
  }
}
