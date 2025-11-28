package com.inventory.inventory_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class product {

    @Id
    @Column(nullable = false)
    @NotBlank(message = "El ID es obligatorio")
    private String id;  // TEXT PRIMARY KEY

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String name;  // TEXT NOT NULL

    private String description; // TEXT (nullable)

    @NotBlank(message = "La categoría es obligatoria")
    private String category; // TEXT NOT NULL

    @Column(nullable = false)
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private Double price; // NUMERIC(10,2) NOT NULL

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El SKU es obligatorio")
    private String sku; // TEXT NOT NULL UNIQUE
}
