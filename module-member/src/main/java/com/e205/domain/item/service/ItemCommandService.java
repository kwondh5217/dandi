package com.e205.domain.item.service;

import com.e205.domain.item.dto.CreateItemCommand;
import com.e205.domain.item.dto.UpdateItemCommand;
import com.e205.domain.item.dto.UpdateItemOrderCommand;

public interface ItemCommandService {

  void save(CreateItemCommand createItemCommand);

  void update(UpdateItemCommand updateCommand);

  void updateItemOrder(UpdateItemOrderCommand updateItemOrderCommand);
}