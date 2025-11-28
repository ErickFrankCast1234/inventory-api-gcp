package com.inventory.inventory_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class inventory {

    @Id
    @Column(nullable = false)
    private String id; // TEXT PRIMARY KEY

    @Column(name = "product_id", nullable = false)
    private String productId; // TEXT NOT NULL

    @Column(name = "store_id", nullable = false)
    private String storeId; // TEXT NOT NULL

    @Column(nullable = false)
    private Integer quantity; // INTEGER NOT NULL

    @Column(name = "min_stock", nullable = false)
    private Integer minStock; // INTEGER NOT NULL
}