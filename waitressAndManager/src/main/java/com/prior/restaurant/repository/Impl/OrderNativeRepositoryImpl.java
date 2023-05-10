package com.prior.restaurant.repository.Impl;

import com.prior.restaurant.models.OrderModel;
import com.prior.restaurant.repository.OrderNativeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class OrderNativeRepositoryImpl implements OrderNativeRepository {
    private final JdbcTemplate jdbcTemplate;

    public OrderNativeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<OrderModel> findOrderDynamic(OrderModel findOrderModel) {
        List<Object> paramList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT  num_table, id,num_of_serve,status_order");
        sb.append("  FROM order_entity ");
        sb.append(" WHERE 1=1 ");
        if (findOrderModel.getOrderId()!=null) {
            sb.append(" and id = ? ");
            paramList.add(findOrderModel.getOrderId());
        }
        if (findOrderModel.getNumTable()!=0) {
            sb.append(" and num_table = ? ");
            paramList.add(findOrderModel.getNumTable());
        }
        if (!StringUtils.isEmpty(findOrderModel.getStatusOrder())) {
            sb.append(" and status_order = ? ");
            paramList.add(findOrderModel.getStatusOrder());
        }
        if (findOrderModel.getNumOfServe() !=0) {
            sb.append(" and num_of_serve = ? ");
            paramList.add(findOrderModel.getNumOfServe() );
        }

        String sql = sb.toString();
        log.info("sql {}" , sql);

        List<OrderModel> result = this.jdbcTemplate.query(sql, new RowMapper<OrderModel>() {
            @Override
            public OrderModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                OrderModel orderModel = new OrderModel();
                int col = 1;
                orderModel.setNumTable(rs.getInt(col++));
                orderModel.setOrderId(rs.getInt(col++));
                orderModel.setNumOfServe(rs.getInt(col++));
                orderModel.setStatusOrder(rs.getString(col));
                return orderModel;
            }
        },paramList.toArray());

        log.info("result {}" , result);

        return result;
    }
}
