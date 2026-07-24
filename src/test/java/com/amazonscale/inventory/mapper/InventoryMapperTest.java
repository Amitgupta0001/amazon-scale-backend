package com.amazonscale.inventory.mapper;

import com.amazonscale.inventory.dto.InventoryRequest;
import com.amazonscale.inventory.dto.InventoryResponse;
import com.amazonscale.inventory.dto.InventoryUpdateRequest;
import com.amazonscale.inventory.entity.Inventory;
import com.amazonscale.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryMapperTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {

        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .build();

        inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .quantity(100)
                .reservedQuantity(20)
                .warehouseLocation("Warehouse A")
                .lowStockThreshold(10)
                .build();
    }

    @Test
    void shouldConvertRequestToInventory() {

        InventoryRequest request = new InventoryRequest();

        request.setProductId(1L);
        request.setQuantity(150);
        request.setWarehouseLocation("Warehouse B");
        request.setLowStockThreshold(15);

        Inventory entity =
                InventoryMapper.toInventory(request);

        assertNotNull(entity);

        assertEquals(150,
                entity.getQuantity());

        assertEquals("Warehouse B",
                entity.getWarehouseLocation());

        assertEquals(15,
                entity.getLowStockThreshold());
    }


    @Test
    void shouldConvertInventoryToResponse() {

        InventoryResponse response =
                InventoryMapper.toResponse(inventory);

        assertNotNull(response);

        assertEquals(1L,
                response.getId());

        assertEquals(1L,
                response.getProductId());

        assertEquals("Laptop",
                response.getProductName());

        assertEquals(100,
                response.getQuantity());

        assertEquals(20,
                response.getReservedQuantity());

        assertEquals(80,
                response.getAvailableQuantity());

        assertEquals("Warehouse A",
                response.getWarehouseLocation());

        assertEquals(10,
                response.getLowStockThreshold());
    }
    @Test
    void shouldUpdateInventoryEntity() {

        InventoryUpdateRequest request =
                InventoryUpdateRequest.builder()
                        .quantity(200)
                        .warehouseLocation("Warehouse C")
                        .lowStockThreshold(25)
                        .build();

        InventoryMapper.updateInventory(
                inventory,
                request
        );

        assertEquals(
                200,
                inventory.getQuantity()
        );

        assertEquals(
                "Warehouse C",
                inventory.getWarehouseLocation()
        );

        assertEquals(
                25,
                inventory.getLowStockThreshold()
        );
    }

    @Test
    void shouldCalculateAvailableQuantity() {

        inventory.setQuantity(120);
        inventory.setReservedQuantity(35);

        assertEquals(
                85,
                inventory.getAvailableQuantity()
        );
    }

    @Test
    void shouldReturnZeroWhenReservedGreaterThanQuantity() {

        inventory.setQuantity(50);
        inventory.setReservedQuantity(70);

        assertEquals(
                0,
                inventory.getAvailableQuantity()
        );
    }

    @Test
    void shouldHandleZeroReservedQuantity() {

        inventory.setReservedQuantity(0);

        InventoryResponse response =
                InventoryMapper.toResponse(inventory);

        assertEquals(
                100,
                response.getAvailableQuantity()
        );
    }

    @Test
    void shouldNotModifyProductWhileUpdating() {

        Product oldProduct = inventory.getProduct();

        InventoryUpdateRequest request =
                InventoryUpdateRequest.builder()
                        .quantity(300)
                        .warehouseLocation("Warehouse D")
                        .lowStockThreshold(30)
                        .build();

        InventoryMapper.updateInventory(
                inventory,
                request
        );

        assertEquals(
                oldProduct,
                inventory.getProduct()
        );
    }

}