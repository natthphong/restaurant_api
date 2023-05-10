package com.prior.restaurant.controller.rest;


import com.prior.restaurant.exception.BaseException;
import com.prior.restaurant.models.InsertOrderModel;
import com.prior.restaurant.models.MenuModel;
import com.prior.restaurant.models.ResponseModel;
import com.prior.restaurant.service.WaitressService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/waitress/api")
public class WaitressController {
    private final WaitressService waitressService;

    public WaitressController(WaitressService waitressService) {
        this.waitressService = waitressService;
    }

    @PostMapping(path = "/insertOrder" )
    public ResponseModel<String> insertOrder(@RequestBody InsertOrderModel insertOrderModel) throws BaseException {
        return waitressService.insertOrder(insertOrderModel);
    }
    @GetMapping(path = "/find/menu/CookedToServe")
    public ResponseModel<List<MenuModel>> findOrderCookedToServe() throws BaseException {
        return waitressService.findOrderCookedToServe();
    }
    @PostMapping(path = "/update/status/serve")
    public ResponseModel<String> updateOrderServe(@RequestBody MenuModel orderModel) throws BaseException {
        return  waitressService.updateOrderServe(orderModel);
    }

}
