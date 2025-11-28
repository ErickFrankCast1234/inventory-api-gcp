package com.inventory.inventory_api.controller;

import com.inventory.inventory_api.model.inventory;
import com.inventory.inventory_api.model.movement;
import com.inventory.inventory_api.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{storeId}/inventory")
    public ResponseEntity<?> getInventoryByStore(@PathVariable String storeId) {
        try {
            List<inventory> result = inventoryService.getInventoryByStore(storeId);
            return ResponseEntity.ok(result);
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<movement> transfer(@RequestBody movement request) {
        movement mov = inventoryService.transfer(request);
        return ResponseEntity.ok(mov);
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<inventory>> getLowStockAlerts() {
        return ResponseEntity.ok(inventoryService.getLowStockAlerts());
    }
}
