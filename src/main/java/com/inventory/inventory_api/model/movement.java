package com.inventory.inventory_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class movement {

    @Id
    private String id;  // TEXT PRIMARY KEY

    @Column(name = "product_id", nullable = false)
    private String productId;  // TEXT NOT NULL

    @Column(name = "source_store_id")
    private String sourceStoreId;  // TEXT NULL

    @Column(name = "target_store_id")
    private String targetStoreId;  // TEXT NULL

    @Column(nullable = false)
    private Integer quantity;  // INTEGER NOT NULL

    @Column(nullable = false)
    private LocalDateTime timestamp;  // TIMESTAMP NOT NULL

    @Column(nullable = false)
    private String type;  // TEXT NOT NULL (IN, OUT, TRANSFER)
}