package com.inventory.inventory_api;

import com.inventory.inventory_api.service.*;
import com.inventory.inventory_api.model.inventory;
import com.inventory.inventory_api.model.product;
import com.inventory.inventory_api.repository.InventoryRepository;
import com.inventory.inventory_api.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private ProductService productService;

    private product p1;
    private product p2;

    @BeforeEach
    void setup() {
        p1 = new product(
                "1",
                "Laptop",
                "desc",
                "CategoryA",
                1000.0,
                "SKU123");

        p2 = new product(
                "2",
                "Teclado",
                "desc",
                "CategoryB",
                50.0,
                "SKU999");
    }

    // ---------------------------------------------------------------------
    // getProducts()
    // ---------------------------------------------------------------------

    @Test
    void testGetProducts_FilterByCategory() {
        Page<product> page = new PageImpl<>(List.of(p1, p2));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<product> result = productService.getProducts(
                "CategoryA", null, null, null, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals("CategoryA", result.getContent().get(0).getCategory());
    }

    @Test
    void testGetProducts_FilterByPriceRange() {
        Page<product> page = new PageImpl<>(List.of(p1, p2));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<product> result = productService.getProducts(
                null, 100.0, 2000.0, null, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals("1", result.getContent().get(0).getId());
    }

    @Test
    void testGetProducts_FilterByStock() {

        // Página con 2 productos
        Page<product> page = new PageImpl<>(List.of(p1, p2));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        // p1 → total stock = 50
        when(inventoryRepository.findByProductId("1"))
                .thenReturn(List.of(
                        new inventory("inv1", "1", "store1", 50, 5)));

        // p2 → total stock = 1
        when(inventoryRepository.findByProductId("2"))
                .thenReturn(List.of(
                        new inventory("inv2", "2", "store1", 1, 5)));

        // Ejecutamos el servicio con minStock = 10
        Page<product> result = productService.getProducts(
                null, null, null, 10, 0, 10);

        // Validamos
        assertEquals(1, result.getContent().size());
        assertEquals("1", result.getContent().get(0).getId());
    }

    // ---------------------------------------------------------------------
    // getProductById()
    // ---------------------------------------------------------------------

    @Test
    void testGetProductById_Success() {
        when(productRepository.findById("1")).thenReturn(Optional.of(p1));

        product found = productService.getProductById("1");

        assertEquals("Laptop", found.getName());
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById("X")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productService.getProductById("X"));
    }

    // ---------------------------------------------------------------------
    // createProduct()
    // ---------------------------------------------------------------------

    @Test
    void testCreateProduct_Success() {
        when(productRepository.existsById("1")).thenReturn(false);
        when(productRepository.findBySku("SKU123")).thenReturn(Optional.empty());
        when(productRepository.save(any(product.class))).thenReturn(p1);

        product saved = productService.createProduct(p1);

        assertEquals("Laptop", saved.getName());
        verify(productRepository).save(p1);
    }

    @Test
    void testCreateProduct_IdExists() {
        when(productRepository.existsById("1")).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> productService.createProduct(p1));
        verify(productRepository, never()).save(any());
    }

    @Test
    void testCreateProduct_SkuExists() {
        when(productRepository.existsById("1")).thenReturn(false);
        when(productRepository.findBySku("SKU123")).thenReturn(Optional.of(p1));

        assertThrows(RuntimeException.class,
                () -> productService.createProduct(p1));
        verify(productRepository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // updateProduct()
    // ---------------------------------------------------------------------

    @Test
    void testUpdateProduct_Success() {
        when(productRepository.findById("1")).thenReturn(Optional.of(p1));
        when(productRepository.save(any(product.class))).thenReturn(p1);

        product updated = productService.updateProduct("1", p1);

        assertNotNull(updated);
        verify(productRepository).save(any(product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        when(productRepository.findById("X")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> productService.updateProduct("X", p1));
        verify(productRepository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // deleteProduct()
    // ---------------------------------------------------------------------

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.existsById("1")).thenReturn(true);

        productService.deleteProduct("1");

        verify(productRepository).deleteById("1");
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.existsById("X")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> productService.deleteProduct("X"));
        verify(productRepository, never()).deleteById(any());
    }

    

    
}
