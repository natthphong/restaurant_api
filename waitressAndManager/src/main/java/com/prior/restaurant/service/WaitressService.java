package com.prior.restaurant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.prior.restaurant.component.OrderTransformComponent;
import com.prior.restaurant.component.kafka.kafkacomponent.KafkaProducerComponent;
import com.prior.restaurant.entity.MenuEntity;
import com.prior.restaurant.entity.OrderEntity;
import com.prior.restaurant.exception.BaseException;
import com.prior.restaurant.models.*;
import com.prior.restaurant.repository.Impl.MenuNativeRepositoryImpl;
import com.prior.restaurant.repository.MenuRepository;
import com.prior.restaurant.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class WaitressService {

    @Value("${kafka.topics.new}")
    private String topicNew;
    @Value("${kafka.topics.bill}")
    private String topicBill;

    private final KafkaProducerComponent kafkaProducerComponent;
    private  final OrderRepository orderRepository;
    private final MenuNativeRepositoryImpl menuNativeRepository;
    private  final OrderTransformComponent orderTransformComponent;
    private  final MenuRepository menuRepository;
    public WaitressService(KafkaProducerComponent kafkaProducerComponent, OrderRepository orderRepository, MenuNativeRepositoryImpl menuNativeRepository, OrderTransformComponent orderTransformComponent, MenuRepository menuRepository) {
        this.kafkaProducerComponent = kafkaProducerComponent;
        this.orderRepository = orderRepository;
        this.menuNativeRepository = menuNativeRepository;
        this.orderTransformComponent = orderTransformComponent;
        this.menuRepository = menuRepository;
    }

    public boolean validate(MenuModel menuModel){
        return  !StringUtils.isEmpty(menuModel.getName()) &&
                !StringUtils.isEmpty(menuModel.getType()) &&
                menuModel.getPrice()!=0;
    }

    public boolean validateList(List<MenuModel> menuModels){
        if (menuModels.isEmpty()){
            return false;
        }
        for (MenuModel menuModel: menuModels){
            if(!validate(menuModel)){
                return false;
            }
        }

        return true;
    }
    public ResponseModel<String> insertOrder(InsertOrderModel insertOrderModel)  throws BaseException {
        ResponseModel<String> responseModel = new ResponseModel<>();
        responseModel.setTimestamp(LocalDate.now());
        responseModel.setStatus(200);
        responseModel.setDescription("ok");
        if (!validateList(insertOrderModel.getMenuModels())){
            responseModel.setStatus(400);
            responseModel.setDescription("Invalidate insert order model");
            return responseModel;
        }
        try {OrderEntity orderEntity = new OrderEntity();
            List<MenuEntity> menuEntities = this.orderTransformComponent.listMenuModelToListMenuEntity(insertOrderModel.getMenuModels() , orderEntity);
            orderEntity.setStatusOrder("process");
            orderEntity.setMenuEntity(menuEntities);
            orderEntity.setNumTable(insertOrderModel.getNumTable());
            orderEntity.setNumOfServe(0);
            this.orderRepository.save(orderEntity);
            responseModel.setData("insert success");
            this.kafkaProducerComponent.sendData(String.valueOf(menuEntities.size()) , topicNew);
        }catch (Exception ex){
            throw new BaseException(ex.getMessage());
        }

        return responseModel;
    }


    public ResponseModel<List<MenuModel>> findOrderCookedToServe()throws BaseException {
        MenuModel menuModel = new MenuModel();
        menuModel.setStatus("serving");
        ResponseModel<List<MenuModel>> responseModel = new ResponseModel<>();
        responseModel.setTimestamp(LocalDate.now());
        responseModel.setStatus(200);
        responseModel.setDescription("ok");
        try {
            List<MenuModel> orderModels = this.menuNativeRepository.findMenuDynamic(menuModel);
            responseModel.setData(orderModels);
        }catch (Exception ex){
            throw new BaseException(ex.getMessage());
        }
        return responseModel;
    }

    public ResponseModel<String> updateOrderServe(MenuModel menuModel) throws  BaseException {

        ResponseModel<String> responseModel = new ResponseModel<>();
        responseModel.setTimestamp(LocalDate.now());
        responseModel.setStatus(200);
        responseModel.setDescription("ok");
        try {
            Optional<MenuEntity> menuEntity = this.menuRepository.findById(menuModel.getId());
            if (menuEntity.isEmpty()){
                responseModel.setStatus(404);
                responseModel.setDescription("not found");
                return  responseModel;
            }
            if (menuEntity.get().getStatus().equals("cooking")
            ){
                responseModel.setStatus(400);
                responseModel.setDescription("cooking not yet");
                return  responseModel;
            }
            if ( menuEntity.get().getStatus().equals("served")
            ){
                responseModel.setStatus(400);
                responseModel.setDescription("served already");
                return  responseModel;
            }
            if (menuModel.getStatus().equals("served")){
                menuEntity.get().setStatus("served");
                OrderEntity order = menuEntity.get().getOrder();
                if (order.getNumOfServe() == order.getMenuEntity().size()-1 ){
                    order.setStatusOrder("success");
                    responseModel.setDescription("ok order success already");
                    int serve = order.getNumOfServe()+1;
                    order.setNumOfServe(serve);
                    this.orderRepository.save(order);
                    //TODO
                    String message = getBill(order);
                    log.info("message {} " , message);
                    this.kafkaProducerComponent.sendData(message,topicBill);
                }else {
                    int serve = order.getNumOfServe()+1;
                    order.setNumOfServe(serve);
                    this.orderRepository.save(order);
                }

            }

            int numUpdate = this.menuNativeRepository.updateMenuStatus(menuModel);
            if (numUpdate!=0) {
                responseModel.setData("update success");
                responseModel.setTimestamp(LocalDate.now());
            }else {
                throw  new BaseException("update failed");
            }
            log.info("{}" , numUpdate);
        }catch (Exception ex){
            throw new BaseException(ex.getMessage());
        }
        return responseModel;
    }

    private String getBill(OrderEntity order) throws JsonProcessingException {
        BillModel billModel = new BillModel();
        billModel.setUuid(getUUID());
        billModel.setOrderId(order.getId());
        List<MenuModel> menuModels = this.orderTransformComponent.listMenuEntityToListMenuModel(order.getMenuEntity());
        String menu = new Gson().toJson(menuModels);
        billModel.setMenuList(menu);
        billModel.setNumTable(order.getNumTable());
        String message = objectToJsonString(billModel);
        return message;
    }

    public void updateMenuToServing(String message){

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MenuModel menuModel = objectMapper.readValue(message,MenuModel.class);
            ///notice
            String notice = "\n"+"=".repeat(50)+"\n menu id "+menuModel.getId()+" : "+ menuModel.getName()
                    + " already cooked \n going to serve in table "+ menuModel.getNumTable()+"\n"+"=".repeat(50);

            log.info(" {}" , notice);
            //update
            menuModel.setStatus("serving");
            this.updateOrderServe(menuModel);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private String objectToJsonString(BillModel model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(model);
    }

    public static String getUUID() {
        Date date = new Date();
        long time = date.getTime();

        return "BILL-" + time;
    }

}
