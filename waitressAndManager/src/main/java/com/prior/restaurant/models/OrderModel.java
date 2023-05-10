package com.prior.restaurant.models;

import lombok.Data;

@Data
public class OrderModel {
    private Integer orderId;
    private int numTable;
    private  String statusOrder;
    private int numOfServe;
}
