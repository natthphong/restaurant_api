package com.prior.restaurant.repository.Impl;

import com.prior.restaurant.models.IncomeModel;
import com.prior.restaurant.repository.BillNativeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Repository
@Slf4j
public class BillNativeRepositoryImpl implements BillNativeRepository {
    private final JdbcTemplate jdbcTemplate;

    public BillNativeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int getIncome(IncomeModel incomeModel) {

        final Integer[] income = {0};

        log.info("inside getIncome {}" , incomeModel);

        List<Object> paramList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT  sum(total)  ");
        sb.append("  FROM  bill_entity ");
        sb.append(" WHERE 1=1 ");

        if (null != incomeModel.getStartDate() && null == incomeModel.getEndDate()) {
            sb.append(" and time_stamp >= ? ");
            paramList.add(incomeModel.getStartDate());
        }
        if (null != incomeModel.getStartDate()  && null != incomeModel.getEndDate()) {
            sb.append(" and time_stamp between ? and ? ");
            paramList.add(incomeModel.getStartDate() );
            paramList.add(incomeModel.getEndDate());

        }

        String sql = sb.toString();
       jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                income[0] = rs.getInt(1);
            }
        },paramList.toArray());

       log.info("Sql {}" , sql);
        return income[0];
    }
}
