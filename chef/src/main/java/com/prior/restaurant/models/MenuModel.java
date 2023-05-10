package com.prior.restaurant.models;

import com.prior.restaurant.entity.MenuEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MenuModel {

    private  Integer id;
    private  String name;
    private String status;
    private String type;
    private int numTable;
    private Integer orderId;
    private int price;


}
