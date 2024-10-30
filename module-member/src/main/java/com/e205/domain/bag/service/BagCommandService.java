package com.e205.domain.bag.service;

import com.e205.domain.bag.dto.BagItemOrderUpdateCommand;
import com.e205.domain.bag.dto.BagNameUpdateCommand;
import com.e205.domain.bag.dto.BagOrderUpdateCommand;
import com.e205.domain.bag.dto.CopyBagCommand;
import com.e205.domain.bag.dto.CopyBagResponse;
import com.e205.domain.bag.dto.CreateBagCommand;
import com.e205.domain.bag.dto.SelectBagCommand;

public interface BagCommandService {

  void save(CreateBagCommand createBagCommand);

  void updateBagOrder(BagOrderUpdateCommand bagOrderUpdateCommand);

  void updateBagName(BagNameUpdateCommand bagNameUpdateCommand);

  void selectBag(SelectBagCommand command);

  void updateBagItemOrder(BagItemOrderUpdateCommand command);

  CopyBagResponse copyBag(CopyBagCommand command);
}
