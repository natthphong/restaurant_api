package com.prior.restaurant.controller.rest;

import com.prior.restaurant.exception.BaseException;
import com.prior.restaurant.models.MenuModel;
import com.prior.restaurant.models.ResponseModel;
import com.prior.restaurant.service.ChefService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chef/api")
public class ChefController {
    private final ChefService chefService;

    public ChefController(ChefService chefService) {
        this.chefService = chefService;
    }

    @GetMapping(path = "/find/menu/ToCooking")
    public ResponseModel<List<MenuModel>> findOrderToCooking(@RequestParam(name = "type") String type) throws BaseException {
        return chefService.findMenuToCooking(type);
    }

    @PostMapping(path = "/update/status/cook")
    public ResponseModel<String> updateStatusCook(@RequestBody MenuModel orderModel) throws  BaseException{
        return  this.chefService.updateStatusCook(orderModel);
    }

}
