package com.e205.base.item.service;

import com.e205.base.item.command.FoundItemSaveCommand;
import com.e205.base.item.command.FoundItemDeleteCommand;

public interface FoundItemCommandService {

  void save(FoundItemSaveCommand command);

  void delete(FoundItemDeleteCommand command);
}
