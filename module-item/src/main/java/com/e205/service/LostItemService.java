package com.e205.service;

import com.e205.command.LostItemSaveCommand;

public interface LostItemService {

	void save(LostItemSaveCommand command);
}
