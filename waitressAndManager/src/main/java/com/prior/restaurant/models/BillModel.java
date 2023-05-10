package com.prior.restaurant.models;

import lombok.Data;

import java.time.LocalDate;


@Data
public class BillModel {
    private String uuid;
    private Integer orderId;
    private Integer numTable;
    private String menuList;
    private Integer total;
    private LocalDate timeStamp;

}
