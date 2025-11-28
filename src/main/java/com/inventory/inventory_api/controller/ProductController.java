package com.inventory.inventory_api.controller;

import com.inventory.inventory_api.model.*;
import com.inventory.inventory_api.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products
     * Filtros: category, minPrice, maxPrice, minStock
     * Paginación: page, size
     */
    @GetMapping
    public ResponseEntity<PageResponse<product>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<product> result = productService.getProducts(
                category, minPrice, maxPrice, minStock, page, size
        );

        PageResponse<product> response = new PageResponse<>(
                result.getContent(),
                page,
                size,
                result.getTotalElements(),
                result.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<product> getProductById(@PathVariable String id) {
        product p = productService.getProductById(id);
        return ResponseEntity.ok(p);
    }

    @PostMapping
    public ResponseEntity<product> createProduct(@Valid @RequestBody product p) {
        product saved = productService.createProduct(p);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<product> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody product body) {

        product updated = productService.updateProduct(id, body);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}