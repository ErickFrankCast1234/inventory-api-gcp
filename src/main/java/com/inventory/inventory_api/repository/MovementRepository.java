package com.inventory.inventory_api.repository;

import com.inventory.inventory_api.model.movement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepository extends JpaRepository<movement, String> {
}