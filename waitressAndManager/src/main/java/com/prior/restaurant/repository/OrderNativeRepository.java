package com.prior.restaurant.repository;

import com.prior.restaurant.models.MenuModel;
import com.prior.restaurant.models.OrderModel;

import java.util.List;

public interface OrderNativeRepository {
    public List<OrderModel> findOrderDynamic(OrderModel findOrderModel);
}
