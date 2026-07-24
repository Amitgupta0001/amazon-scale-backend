package com.amazonscale.inventory.service;

import com.amazonscale.inventory.dto.InventoryRequest;
import com.amazonscale.inventory.dto.InventoryResponse;
import com.amazonscale.inventory.dto.InventoryUpdateRequest;

import java.util.List;

public interface InventoryService {

    InventoryResponse createInventory(InventoryRequest request);
    InventoryResponse updateInventory(Long id , InventoryUpdateRequest request);

    InventoryResponse getInventoryById(Long id);

    InventoryResponse getInventoryByProductId(Long productId);

    List<InventoryResponse> getAllInventory();

    void deleteInventoryById(Long id);
}
