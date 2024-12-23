package com.e205.base.item.service;

import com.e205.base.item.command.LostItemGrantCommand;
import com.e205.base.item.command.LostItemSaveCommand;
import com.e205.base.item.command.LostItemDeleteCommand;

public interface LostItemCommandService {

  void save(LostItemSaveCommand command);

  void grant(LostItemGrantCommand command);

  void delete(LostItemDeleteCommand command);
}
