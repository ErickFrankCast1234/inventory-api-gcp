package com.inventory.inventory_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.inventory.inventory_api.model.product;

public interface ProductRepository
                extends JpaRepository<product, String>, JpaSpecificationExecutor<product> {

        Optional<product> findBySku(String sku);

}