package com.inventory.inventory_api.service;

import com.inventory.inventory_api.model.inventory;
import com.inventory.inventory_api.model.movement;
import com.inventory.inventory_api.repository.InventoryRepository;
import com.inventory.inventory_api.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final MovementRepository movementRepository;

    public List<inventory> getInventoryByStore(String storeId) {

        List<inventory> result = inventoryRepository.findByStoreId(storeId);

        if (result.isEmpty()) {
            throw new NoSuchElementException("No hay inventario para esta tienda");
        }

        return result;
    }

    /**
     * Transfiere stock entre tiendas.
     */
    public movement transfer(movement request) {

        // ==================================
        // 1. VALIDAR INVENTARIO EN TIENDA ORIGEN
        // ==================================
        inventory sourceInv = inventoryRepository.findByProductIdAndStoreId(
                request.getProductId(),
                request.getSourceStoreId()).orElseThrow(() -> new RuntimeException("Inventario de origen no existe"));

        if (sourceInv.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Stock insuficiente en la tienda origen");
        }

        // ==================================
        // 2. OBTENER / CREAR INVENTARIO DESTINO
        // ==================================
        inventory targetInv = inventoryRepository.findByProductIdAndStoreId(
                request.getProductId(),
                request.getTargetStoreId()).orElseGet(() -> {
                    inventory inv = new inventory();
                    inv.setId(UUID.randomUUID().toString());
                    inv.setProductId(request.getProductId());
                    inv.setStoreId(request.getTargetStoreId());
                    inv.setQuantity(0);
                    inv.setMinStock(0);
                    return inv;
                });

        // ==================================
        // 3. ACTUALIZAR STOCK
        // ==================================
        sourceInv.setQuantity(sourceInv.getQuantity() - request.getQuantity());
        targetInv.setQuantity(targetInv.getQuantity() + request.getQuantity());

        inventoryRepository.save(sourceInv);
        inventoryRepository.save(targetInv);

        // ==================================
        // 4. GUARDAR MOVIMIENTO
        // ==================================
        movement mov = new movement();
        mov.setId(UUID.randomUUID().toString());
        mov.setProductId(request.getProductId());
        mov.setSourceStoreId(request.getSourceStoreId());
        mov.setTargetStoreId(request.getTargetStoreId());
        mov.setQuantity(request.getQuantity());
        mov.setTimestamp(LocalDateTime.now());
        mov.setType("TRANSFER");

        movementRepository.save(mov); // ✅ CORRECTO

        return mov;
    }

    public List<inventory> getLowStockAlerts() {
        return inventoryRepository.findLowStock();
    }

}
