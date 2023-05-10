package com.prior.restaurant.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;


@Entity
@Data
@NoArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private int numTable;
    @OneToMany(mappedBy = "order",cascade = { CascadeType.PERSIST,
            CascadeType.MERGE }, fetch = FetchType.EAGER, orphanRemoval = true)
    @ToString.Exclude
    private List<MenuEntity> menuEntity;
    private  String statusOrder;

    private int numOfServe;
}
