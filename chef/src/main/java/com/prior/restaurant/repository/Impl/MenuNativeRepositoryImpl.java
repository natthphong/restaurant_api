package com.prior.restaurant.repository.Impl;

import com.prior.restaurant.models.MenuModel;
import com.prior.restaurant.repository.MenuNativeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Repository
@Slf4j
public class MenuNativeRepositoryImpl  implements MenuNativeRepository {
    private final JdbcTemplate jdbcTemplate;

    public MenuNativeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MenuModel> findMenuDynamic(MenuModel menuModel) {

        List<Object> paramList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT  o.num_table, m.id,m.name,m.status,m.type ,o.id,m.price ");
        sb.append("  FROM order_entity o , menu_entity m ");
        sb.append(" WHERE 1=1 and o.id = m.order_id");

        if (menuModel.getOrderId() != null && menuModel.getOrderId() !=0 ) {
            sb.append(" and o.id = ? ");
            paramList.add(menuModel.getOrderId());
        }
        if (!StringUtils.isEmpty(menuModel.getType())) {
            sb.append(" and m.type = ? ");
            paramList.add(menuModel.getType());
        }
        if (!StringUtils.isEmpty(menuModel.getStatus())) {
            sb.append(" and m.status = ? ");
            paramList.add(menuModel.getStatus());
        }
        if (menuModel.getNumTable() !=0) {
            sb.append(" and num_table = ? ");
            paramList.add(menuModel.getNumTable());
        }
        if (menuModel.getId() !=null){
            sb.append(" and m.id = ? ");
            paramList.add(menuModel.getId());
        }

        String sql = sb.toString();
        log.info("sql {}" , sql);

        List<MenuModel> result = this.jdbcTemplate.query(sql, new RowMapper<MenuModel>() {
            @Override
            public MenuModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                MenuModel menuModel1 = new MenuModel();
                int col = 1;
                menuModel1.setNumTable(rs.getInt(col++));
                menuModel1.setId(rs.getInt(col++));
                menuModel1.setName(rs.getString(col++));
                menuModel1.setStatus(rs.getString(col++));
                menuModel1.setType(rs.getString(col++));
                menuModel1.setOrderId(rs.getInt(col++));
                menuModel1.setPrice(rs.getInt(col));
                return menuModel1;
            }
        },paramList.toArray());

        log.info("result {}" , result);

        return result;
    }

    @Override
    public int updateMenuStatus(MenuModel menuModel) {
        log.info("inside update {}" , menuModel);
        List<Object> paramList = new ArrayList<>();
        StringJoiner stringJoiner = new StringJoiner(",");
        int insertRow= 0;
        String sql = " UPDATE menu_entity set ";

        if (!StringUtils.isEmpty(menuModel.getStatus()) ){
            stringJoiner.add(" status = ? ");
            paramList.add(menuModel.getStatus());
        }

        if(paramList.size()>0 && null!= menuModel.getId()){
            sql += stringJoiner.toString();
            sql += " WHERE id = ? ";
            paramList.add(menuModel.getId());
            log.info("Inside sql {} " , sql);
            insertRow = this.jdbcTemplate.update(sql, paramList.toArray());
        }

        return insertRow;
    }
}
