package com.prior.restaurant.repository;

import com.prior.restaurant.models.MenuModel;

import java.util.List;

public interface MenuNativeRepository {
    public List<MenuModel> findMenuDynamic(MenuModel menuModel);

    public int updateMenuStatus(MenuModel orderModel);

}
