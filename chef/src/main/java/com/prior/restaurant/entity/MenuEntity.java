package com.prior.restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Integer id;
    private String name;
    private String status;
    private String type;
    private int price;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name="order_id", nullable=false)
    private OrderEntity order;

}
