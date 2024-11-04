package com.e205.service;

import com.e205.command.LostItemDeleteCommand;
import com.e205.command.LostItemSaveCommand;

public interface LostItemCommandService {

  void save(LostItemSaveCommand command);

  void delete(LostItemDeleteCommand command);
}
