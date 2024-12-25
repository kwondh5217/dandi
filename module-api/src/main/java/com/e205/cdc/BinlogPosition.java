package com.e205.cdc;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BinlogPosition implements Serializable {
  private static final long serialVersionUID = 1L;

  private String binlogFileName;
  private Long binlogPosition;
}