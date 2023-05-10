package com.prior.restaurant.service;

import com.prior.restaurant.exception.BaseException;
import com.prior.restaurant.models.MenuModel;
import com.prior.restaurant.models.OrderModel;
import com.prior.restaurant.models.ResponseModel;
import com.prior.restaurant.repository.Impl.MenuNativeRepositoryImpl;
import com.prior.restaurant.repository.Impl.OrderNativeRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class ManagerService {
    private final OrderNativeRepositoryImpl orderNativeRepository;
    private final MenuNativeRepositoryImpl menuNativeRepository;

    public ManagerService(OrderNativeRepositoryImpl orderNativeRepository, MenuNativeRepositoryImpl menuNativeRepository) {
        this.orderNativeRepository = orderNativeRepository;
        this.menuNativeRepository = menuNativeRepository;
    }

    public ResponseModel<List<MenuModel>> findMenu(MenuModel menuModel)  throws BaseException {
        ResponseModel<List<MenuModel>> responseModel = new ResponseModel<>();
        responseModel.setStatus(200);
        responseModel.setDescription("ok");
        try {
            List<MenuModel> orderModels = menuNativeRepository.findMenuDynamic(menuModel);
            responseModel.setData(orderModels);
            responseModel.setTimestamp(LocalDate.now());
        }catch (Exception ex){
            throw new BaseException(ex.getMessage());
        }
        return responseModel;
    }

    public ResponseModel<List<OrderModel>> findOrder(OrderModel orderModel)throws BaseException {
        ResponseModel<List<OrderModel>> responseModel = new ResponseModel<>();
        responseModel.setStatus(200);
        responseModel.setDescription("ok");
        try {
            List<OrderModel> orderModels = orderNativeRepository.findOrderDynamic(orderModel);
            responseModel.setData(orderModels);
            responseModel.setTimestamp(LocalDate.now());
        }catch (Exception ex){
            throw new BaseException(ex.getMessage());
        }
        return responseModel;
    }
}