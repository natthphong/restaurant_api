package com.prior.restaurant.component;

import com.prior.restaurant.entity.MenuEntity;
import com.prior.restaurant.entity.OrderEntity;
import com.prior.restaurant.models.MenuModel;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class OrderTransformComponent {
    public MenuEntity menuModelToMenuEntity(MenuModel menuModel , OrderEntity orderEntity){
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setName(menuModel.getName());
        menuEntity.setType(menuModel.getType());
        menuEntity.setStatus("new");
        menuEntity.setOrder(orderEntity);
        menuEntity.setPrice(menuModel.getPrice());
        return menuEntity;
    }
    public MenuModel menuEntityToMenuModel(MenuEntity menuEntity){
        MenuModel model = new MenuModel();
        model.setOrderId(menuEntity.getOrder().getId());
        model.setId(menuEntity.getId());
        model.setName(menuEntity.getName());
        model.setType(menuEntity.getType());
        model.setNumTable(menuEntity.getOrder().getNumTable());
        model.setPrice(menuEntity.getPrice());
        model.setStatus(menuEntity.getStatus());
        return model;
    }

    public List<MenuModel> listMenuEntityToListMenuModel(List<MenuEntity> menuEntities){
        List<MenuModel> menuModels = new ArrayList<>();
        for (MenuEntity menuEntity : menuEntities){
            menuModels.add(menuEntityToMenuModel(menuEntity));
        }
        return menuModels;
    }

    public List<MenuEntity> listMenuModelToListMenuEntity(List<MenuModel> menuModels , OrderEntity orderEntity) {
        List<MenuEntity> menuEntities = new ArrayList<>();

        for (MenuModel menuModel : menuModels){
            menuEntities.add(menuModelToMenuEntity(menuModel , orderEntity)) ;
        }
        return menuEntities;

    }
}
