package com.prior.restaurant.models;

import lombok.Data;

import java.util.List;

@Data
public class InsertOrderModel {
    private int numTable;
    private List<MenuModel> menuModels;
}
