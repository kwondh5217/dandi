package com.e205.domain.item.dto;

import java.util.List;

public record UpdateItemOrderCommand(Integer memberId, List<ItemOrder> items) {

}
