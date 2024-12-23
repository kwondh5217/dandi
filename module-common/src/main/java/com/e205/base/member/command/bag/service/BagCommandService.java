package com.e205.base.member.command.bag.service;

import com.e205.base.member.command.bag.command.AddItemsToBagCommand;
import com.e205.base.member.command.bag.command.BagDeleteCommand;
import com.e205.base.member.command.bag.command.BagItemDeleteCommand;
import com.e205.base.member.command.bag.command.BagItemOrderUpdateCommand;
import com.e205.base.member.command.bag.command.BagNameUpdateCommand;
import com.e205.base.member.command.bag.command.BagOrderUpdateCommand;
import com.e205.base.member.command.bag.command.CopyBagCommand;
import com.e205.base.member.command.bag.command.CreateBagCommand;
import com.e205.base.member.command.bag.command.RemoveItemsInBagCommand;
import com.e205.base.member.command.bag.command.SelectBagCommand;
import com.e205.base.member.command.bag.payload.BagPayload;

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
