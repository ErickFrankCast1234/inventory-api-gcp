package com.inventory.inventory_api.service;

import com.inventory.inventory_api.model.inventory;
import com.inventory.inventory_api.model.product;
import com.inventory.inventory_api.repository.InventoryRepository;
import com.inventory.inventory_api.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

        private final ProductRepository productRepository;
        private final InventoryRepository inventoryRepository;

        /**
         * Servicio para filtrar productos con:
         * - category
         * - minPrice
         * - maxPrice
         * - minStock (sumando inventarios)
         * - paginación
         */
        public Page<product> getProducts(
                        String category,
                        Double minPrice,
                        Double maxPrice,
                        Integer minStock,
                        int page,
                        int size) {
                Pageable pageable = PageRequest.of(page, size);

                // Obtener todos los productos (ya que no usamos specifications aquí)
                Page<product> result = productRepository.findAll(pageable);

                // FILTRO POR CATEGORÍA
                if (category != null && !category.isEmpty()) {
                        result = new PageImpl<>(
                                        result.getContent().stream()
                                                        .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                                                        .toList(),
                                        pageable,
                                        result.getTotalElements());
                }

                // FILTRO POR PRECIO MÍNIMO
                if (minPrice != null) {
                        result = new PageImpl<>(
                                        result.getContent().stream()
                                                        .filter(p -> p.getPrice().doubleValue() >= minPrice)
                                                        .toList(),
                                        pageable,
                                        result.getTotalElements());
                }

                // FILTRO POR PRECIO MÁXIMO
                if (maxPrice != null) {
                        result = new PageImpl<>(
                                        result.getContent().stream()
                                                        .filter(p -> p.getPrice().doubleValue() <= maxPrice)
                                                        .toList(),
                                        pageable,
                                        result.getTotalElements());
                }

                // FILTRO POR STOCK
                if (minStock != null) {
                        result = new PageImpl<>(
                                        result.getContent().stream()
                                                        .filter(p -> {
                                                                List<inventory> invList = inventoryRepository
                                                                                .findByProductId(p.getId());
                                                                int totalStock = invList.stream()
                                                                                .mapToInt(inventory::getQuantity)
                                                                                .sum();
                                                                return totalStock >= minStock;
                                                        })
                                                        .toList(),
                                        pageable,
                                        result.getTotalElements());
                }

                return result;
        }

        public product getProductById(String id) {
                return productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        }

        public product createProduct(product newProduct) {

                // Validar ID único
                if (productRepository.existsById(newProduct.getId())) {
                        throw new RuntimeException("Ya existe un producto con el ID: " + newProduct.getId());
                }

                // Validar SKU único
                if (productRepository.findBySku(newProduct.getSku()).isPresent()) {
                        throw new RuntimeException("El SKU ya está en uso: " + newProduct.getSku());
                }

                // Guardar producto
                return productRepository.save(newProduct);
        }

        public product updateProduct(String id, product data) {

                product existing = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

                // Actualizamos solo campos válidos
                existing.setName(data.getName());
                existing.setDescription(data.getDescription());
                existing.setCategory(data.getCategory());
                existing.setPrice(data.getPrice());
                existing.setSku(data.getSku());

                return productRepository.save(existing);
        }

        public void deleteProduct(String id) {

                if (!productRepository.existsById(id)) {
                        throw new RuntimeException("Producto no encontrado");
                }

                productRepository.deleteById(id);
        }

}