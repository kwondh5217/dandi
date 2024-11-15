package com.e205.item.dto;

import static org.junit.jupiter.api.Assertions.*;

import com.e205.command.LostItemSaveCommand;
import org.junit.jupiter.api.Test;

class LostItemCreateRequestTest {

  @Test
  public void test() {
    LostItemSaveCommand command = LostItemCreateRequest.builder()
        .itemDesc("""
            abc
            abc
                        
            abc
                        
                        
            abc
            """)
        .situationDesc("")
        .build().toCommand(1);
    System.out.println(command);
  }
}