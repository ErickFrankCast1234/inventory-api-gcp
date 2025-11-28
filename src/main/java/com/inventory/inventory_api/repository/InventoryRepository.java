package com.inventory.inventory_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.inventory.inventory_api.model.inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<inventory, String> {

    List<inventory> findByProductId(String productId);

    List<inventory> findByStoreId(String storeId);

    Optional<inventory> findByProductIdAndStoreId(String productId, String storeId);

    @Query("SELECT i FROM inventory i WHERE i.quantity < i.minStock")
    List<inventory> findLowStock();

}
