package com.inventory.inventory_api;

import com.inventory.inventory_api.controller.ProductController;
import com.inventory.inventory_api.model.product;
import com.inventory.inventory_api.service.ProductService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    // ---------------------------------------------------------
    // GET /api/products
    // ---------------------------------------------------------
    @Test
    void testGetProducts() throws Exception {

        product p1 = new product("1", "Laptop", "desc", "CategoryA", 1000.0, "SKU1");
        product p2 = new product("2", "Mouse", "desc", "CategoryB", 20.0, "SKU2");

        Page<product> page = new PageImpl<>(List.of(p1, p2));

        Mockito.when(productService.getProducts(
                nullable(String.class),
                nullable(Double.class),
                nullable(Double.class),
                nullable(Integer.class),
                anyInt(),
                anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[1].id").value("2"));
    }

    // ---------------------------------------------------------
    // GET /api/products/{id}
    // ---------------------------------------------------------
    @Test
    void testGetProductById() throws Exception {

        product p = new product("1", "Laptop", "desc", "CategoryA", 1000.0, "SKU1");

        Mockito.when(productService.getProductById("1")).thenReturn(p);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.sku").value("SKU1"));
    }

    // ---------------------------------------------------------
    // POST /api/products
    // ---------------------------------------------------------
    @Test
    void testCreateProduct() throws Exception {

        product p = new product("1", "Laptop", "desc", "CategoryA", 999.0, "SKU1");

        Mockito.when(productService.createProduct(any(product.class)))
                .thenReturn(p);

        String body = """
                {
                    "id": "1",
                    "name": "Laptop",
                    "description": "desc",
                    "category": "CategoryA",
                    "price": 999.0,
                    "sku": "SKU1"
                }
                """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    // ---------------------------------------------------------
    // PUT /api/products/{id}
    // ---------------------------------------------------------
    @Test
    void testUpdateProduct() throws Exception {

        product updated = new product("1", "Laptop X", "desc", "CategoryA", 1200.0, "SKU1");

        Mockito.when(productService.updateProduct(eq("1"), any(product.class)))
                .thenReturn(updated);

        String body = """
                {
                    "id": "1",
                    "name": "Laptop X",
                    "description": "desc",
                    "category": "CategoryA",
                    "price": 1200.0,
                    "sku": "SKU1"
                }
                """;

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop X"))
                .andExpect(jsonPath("$.price").value(1200.0));
    }

    // ---------------------------------------------------------
    // DELETE /api/products/{id}
    // ---------------------------------------------------------
    @Test
    void testDeleteProduct() throws Exception {

        Mockito.doNothing().when(productService).deleteProduct("1");

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    

    @Test
    void testGetProducts_WithFilters() throws Exception {

        product p1 = new product("1", "Laptop", "desc", "A", 1000.0, "SKU1");
        Page<product> page = new PageImpl<>(List.of(p1));

        Mockito.when(productService.getProducts(
                eq("A"), eq(100.0), eq(2000.0), eq(10), eq(0), eq(10))).thenReturn(page);

        mockMvc.perform(get("/api/products")
                .param("category", "A")
                .param("minPrice", "100")
                .param("maxPrice", "2000")
                .param("minStock", "10")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[0].category").value("A"));
    }

    @Test
    void testCreateProduct_InvalidBody_ReturnsBadRequest() throws Exception {

        String body = """
                {
                    "id": "",
                    "name": "",
                    "description": "desc",
                    "category": "",
                    "price": -1,
                    "sku": ""
                }
                """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }


}
