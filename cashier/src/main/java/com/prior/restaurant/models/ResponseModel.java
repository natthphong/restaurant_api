package com.prior.restaurant.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ResponseModel <E>{
    private LocalDate timestamp;
    private int status;
    private String description;
    private E data;

}
