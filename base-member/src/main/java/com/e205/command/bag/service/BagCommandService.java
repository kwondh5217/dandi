package com.e205.command.bag.service;

import com.e205.command.bag.command.AddItemsToBagCommand;
import com.e205.command.bag.command.BagDeleteCommand;
import com.e205.command.bag.command.BagItemDeleteCommand;
import com.e205.command.bag.command.BagItemOrderUpdateCommand;
import com.e205.command.bag.command.BagNameUpdateCommand;
import com.e205.command.bag.command.BagOrderUpdateCommand;
import com.e205.command.bag.command.RemoveItemsInBagCommand;
import com.e205.command.bag.payload.BagPayload;
import com.e205.command.bag.command.CopyBagCommand;
import com.e205.command.bag.command.CreateBagCommand;
import com.e205.command.bag.command.SelectBagCommand;

public interface BagCommandService {

  void save(CreateBagCommand createBagCommand);

  void updateBagOrder(BagOrderUpdateCommand bagOrderUpdateCommand);

  void updateBagName(BagNameUpdateCommand bagNameUpdateCommand);

  void selectBag(SelectBagCommand command);

  void updateBagItemOrder(BagItemOrderUpdateCommand command);

  BagPayload copyBag(CopyBagCommand command);

  void delete(BagDeleteCommand command);

  void deleteBagItem(BagItemDeleteCommand command);

  void addItemToBag(AddItemsToBagCommand command);

  void removeItemsInBag(RemoveItemsInBagCommand command);
}
