package com.prior.restaurant.repository;


import com.prior.restaurant.entity.BillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository  extends JpaRepository<BillEntity , Integer> {
}
