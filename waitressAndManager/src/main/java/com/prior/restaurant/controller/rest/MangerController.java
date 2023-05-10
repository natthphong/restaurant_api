package com.prior.restaurant.controller.rest;


import com.prior.restaurant.exception.BaseException;
import com.prior.restaurant.models.MenuModel;
import com.prior.restaurant.models.OrderModel;
import com.prior.restaurant.models.ResponseModel;
import com.prior.restaurant.service.ManagerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manager/api")
public class MangerController {
    private final ManagerService managerService;

    public MangerController(ManagerService managerService) {
        this.managerService = managerService;
    }


    @GetMapping("/find/menu")
    public ResponseModel<List<MenuModel>> findMenu(@RequestBody MenuModel menuModel) throws BaseException {
        return  managerService.findMenu(menuModel);
    }
    @GetMapping("/find/order")
    public ResponseModel<List<OrderModel>> findOrder(@RequestBody OrderModel orderModel) throws BaseException {
        return  managerService.findOrder(orderModel);
    }
}
