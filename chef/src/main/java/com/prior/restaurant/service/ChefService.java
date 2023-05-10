package com.prior.restaurant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prior.restaurant.component.kafka.component.KafkaProducerComponent;
import com.prior.restaurant.entity.MenuEntity;
import com.prior.restaurant.exception.BaseException;
import com.prior.restaurant.models.MenuModel;
import com.prior.restaurant.models.ResponseModel;
import com.prior.restaurant.repository.Impl.MenuNativeRepositoryImpl;
import com.prior.restaurant.repository.MenuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ChefService {
    private final MenuNativeRepositoryImpl menuNativeRepository;
    private  final MenuRepository menuRepository;

    @Value("${kafka.topics.cook}")
    private String topic;
    private final KafkaProducerComponent kafkaComponent;

    public ChefService(MenuNativeRepositoryImpl menuNativeRepository, MenuRepository menuRepository, KafkaProducerComponent kafkaComponent) {
        this.menuNativeRepository = menuNativeRepository;
        this.menuRepository = menuRepository;
        this.kafkaComponent = kafkaComponent;
    }


    public ResponseModel<List<MenuModel>> findMenuToCooking(String type) throws  BaseException{
        MenuModel menuModel = new MenuModel();
        menuModel.setStatus("cooking");
        menuModel.setType(type);
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

    public ResponseModel<String> updateStatusCook(MenuModel menuModel) throws  BaseException{

        ResponseModel<String> responseModel = new ResponseModel<>();
        responseModel.setStatus(200);
        responseModel.setDescription("ok");
        responseModel.setTimestamp(LocalDate.now());
        try {
            Optional<MenuEntity> orderEntity = this.menuRepository.findById(menuModel.getId());
            if (orderEntity.isEmpty()){
                log.info("not found");
                responseModel.setStatus(404);
                responseModel.setDescription("not found");
                return responseModel;
            }
            if (!orderEntity.get().getStatus().equals("cooking") &&
                    !orderEntity.get().getStatus().equals("new")
            ){
                log.info("cooking");
                responseModel.setStatus(400);
                responseModel.setDescription("cooking already");
                return responseModel;
            }
            int numUpdate = this.menuNativeRepository.updateMenuStatus(menuModel);
            log.info("numUpdate");

            if (numUpdate!=0) {
               // String message = String.valueOf(menuModel.getId());
                menuModel.setNumTable(orderEntity.get().getOrder().getNumTable());
                menuModel.setOrderId(orderEntity.get().getOrder().getId());
                menuModel.setName(orderEntity.get().getName());
                menuModel.setType(orderEntity.get().getType());
                String message = this.objectToJsonString(menuModel);
                log.info("message {}" ,message);
                this.kafkaComponent.sendData(message,this.topic);
                responseModel.setData("update success");
            }else {
                throw  new BaseException("update failed");
            }

            log.info("{}" , numUpdate);

        }catch (Exception ex){
            throw new BaseException(ex.getMessage());
        }

        return responseModel;
    }

    private String objectToJsonString(Object model) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(model);
    }

    public void updateStatusCooking(String size) {

    try {

        String notice = "\n"+"=".repeat(50)+"\n";
        notice += "new " + size + " menu : \n";

        //findNewMenu
        MenuModel model = new MenuModel();
        model.setStatus("new");
        List<MenuModel> menuModels = this.menuNativeRepository.findMenuDynamic(model);
        log.info("model {}" , menuModels);
        if (!menuModels.isEmpty()){
            //updateStatusCooking
            for (MenuModel menuModel : menuModels){
                menuModel.setStatus("cooking");
                notice+= "|menu name : "+ menuModel.getName() +" type : " + menuModel.getType()+" |\n";
                this.menuNativeRepository.updateMenuStatus(menuModel);
            }
        }

         notice += "=".repeat(50)+"\n";
        ///notice
        log.info("notice {}", notice);

    }catch (Exception ex){
        ex.printStackTrace();
    }



    }


}
