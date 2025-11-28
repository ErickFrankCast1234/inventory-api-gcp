package com.inventory.inventory_api;

import com.inventory.inventory_api.model.inventory;
import com.inventory.inventory_api.model.movement;
import com.inventory.inventory_api.repository.InventoryRepository;
import com.inventory.inventory_api.repository.MovementRepository;
import com.inventory.inventory_api.service.InventoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private MovementRepository movementRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private inventory sourceInv;
    private inventory targetInv;
    private movement request;

    @BeforeEach
    void setup() {
        sourceInv = new inventory(
                "inv1",
                "P100",
                "STORE_A",
                50,
                5
        );

        targetInv = new inventory(
                "inv2",
                "P100",
                "STORE_B",
                10,
                5
        );

        request = new movement();
        request.setProductId("P100");
        request.setSourceStoreId("STORE_A");
        request.setTargetStoreId("STORE_B");
        request.setQuantity(20);
    }

    // =====================================================
    // GET INVENTORY BY STORE
    // =====================================================
    @Test
    void testGetInventoryByStore_Success() {

        when(inventoryRepository.findByStoreId("STORE_A"))
                .thenReturn(List.of(sourceInv));

        List<inventory> result = inventoryService.getInventoryByStore("STORE_A");

        assertEquals(1, result.size());
        assertEquals("P100", result.get(0).getProductId());
    }

    @Test
    void testGetInventoryByStore_Empty_NotFound() {

        when(inventoryRepository.findByStoreId("X"))
                .thenReturn(List.of());

        assertThrows(NoSuchElementException.class,
        () -> inventoryService.getInventoryByStore("X"));

    }

    // =====================================================
    // TRANSFER STOCK
    // =====================================================
    @Test
    void testTransfer_Success() {

        when(inventoryRepository.findByProductIdAndStoreId("P100", "STORE_A"))
                .thenReturn(Optional.of(sourceInv));

        when(inventoryRepository.findByProductIdAndStoreId("P100", "STORE_B"))
                .thenReturn(Optional.of(targetInv));

        when(movementRepository.save(any(movement.class)))
                .thenAnswer(inv -> inv.getArgument(0)); // regresa el movimiento

        movement result = inventoryService.transfer(request);

        // VALIDAR STOCK
        assertEquals(30, sourceInv.getQuantity());  // 50 - 20
        assertEquals(30, targetInv.getQuantity());  // 10 + 20

        // VALIDAR MOVIMIENTO
        assertEquals("P100", result.getProductId());
        assertEquals("STORE_A", result.getSourceStoreId());
        assertEquals("STORE_B", result.getTargetStoreId());
        assertEquals(20, result.getQuantity());

        verify(inventoryRepository, times(2)).save(any());
        verify(movementRepository).save(any(movement.class));
    }

    @Test
    void testTransfer_InsufficientStock() {

        sourceInv.setQuantity(5); // < cantidad solicitada

        when(inventoryRepository.findByProductIdAndStoreId("P100", "STORE_A"))
                .thenReturn(Optional.of(sourceInv));

        assertThrows(RuntimeException.class,
                () -> inventoryService.transfer(request));
    }

    @Test
    void testTransfer_TargetStoreInventoryDoesNotExist_CreatesNew() {

        when(inventoryRepository.findByProductIdAndStoreId("P100", "STORE_A"))
                .thenReturn(Optional.of(sourceInv));

        // almacenamiento destino no existe
        when(inventoryRepository.findByProductIdAndStoreId("P100", "STORE_B"))
                .thenReturn(Optional.empty());

        when(movementRepository.save(any(movement.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        movement result = inventoryService.transfer(request);

        // Se crean los nuevos valores
        verify(inventoryRepository, times(2)).save(any(inventory.class));

        assertEquals("P100", result.getProductId());
        assertEquals("STORE_A", result.getSourceStoreId());
        assertEquals("STORE_B", result.getTargetStoreId());
    }
}
