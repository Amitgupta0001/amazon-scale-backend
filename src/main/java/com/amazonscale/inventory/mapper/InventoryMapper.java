package com.amazonscale.inventory.mapper;

import com.amazonscale.inventory.dto.InventoryRequest;
import com.amazonscale.inventory.dto.InventoryResponse;
import com.amazonscale.inventory.dto.InventoryUpdateRequest;
import com.amazonscale.inventory.entity.Inventory;

public final class InventoryMapper {

    private  InventoryMapper() {}

    public static Inventory toInventory(InventoryRequest request) {

        return Inventory.builder()
                .quantity(request.getQuantity())
                .warehouseLocation(request.getWarehouseLocation())
                .lowStockThreshold(request.getLowStockThreshold())
                .build();
    }

    public static InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .warehouseLocation(inventory.getWarehouseLocation())
                .lowStockThreshold(inventory.getLowStockThreshold())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    public static void updateInventory(
            Inventory inventory,
            InventoryUpdateRequest request
    ) {
        inventory.setQuantity(request.getQuantity());
        inventory.setWarehouseLocation(request.getWarehouseLocation());
        inventory.setLowStockThreshold(request.getLowStockThreshold());
    }
}
