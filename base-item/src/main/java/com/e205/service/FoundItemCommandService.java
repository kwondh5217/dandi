package com.e205.service;

import com.e205.command.FoundItemDeleteCommand;
import com.e205.command.FoundItemSaveCommand;

public interface FoundItemCommandService {

  void save(FoundItemSaveCommand command);

  void delete(FoundItemDeleteCommand command);
}
