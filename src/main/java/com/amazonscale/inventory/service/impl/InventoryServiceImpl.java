package com.amazonscale.inventory.service.impl;


import com.amazonscale.inventory.dto.InventoryRequest;
import com.amazonscale.inventory.dto.InventoryResponse;
import com.amazonscale.inventory.dto.InventoryUpdateRequest;
import com.amazonscale.inventory.entity.Inventory;
import com.amazonscale.inventory.mapper.InventoryMapper;
import com.amazonscale.inventory.repository.InventoryRepository;
import com.amazonscale.inventory.service.InventoryService;
import com.amazonscale.product.entity.Product;
import com.amazonscale.product.exception.ProductNotFoundException;
import com.amazonscale.product.repository.ProductRepository;
import com.amazonscale.inventory.exception.InventoryNotFoundException;
import com.amazonscale.inventory.exception.InventoryAlreadyExistsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.amazonscale.inventory.exception.InsufficientStockException;

import java.util.List;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;


    public InventoryServiceImpl(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository
    ) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    //Create Inventory
    @Override
    public InventoryResponse createInventory(InventoryRequest request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException(request.getProductId()));

        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new InventoryAlreadyExistsException(request.getProductId());
        }

        Inventory inventory = InventoryMapper.toInventory(request);
        inventory.setProduct(product);

        Inventory savedInventory = inventoryRepository.save(inventory);

        return InventoryMapper.toResponse(savedInventory);
    }

    //Update Inventory
    @Override
    public InventoryResponse updateInventory(Long id ,InventoryUpdateRequest request){

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(()->new InventoryNotFoundException(id));

        if (request.getQuantity() < inventory.getReservedQuantity()) {
            throw new InsufficientStockException(
                    "Quantity cannot be less than reserved quantity."
            );
        }

        InventoryMapper.updateInventory(inventory, request);
        Inventory updated = inventoryRepository.save(inventory);
        return InventoryMapper.toResponse(updated);
    }

    //Getting Inventory by Id
    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(()->new InventoryNotFoundException(id));

        return InventoryMapper.toResponse(inventory);
    }

    //Getting Inventory by ProductId
    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(
                                "Inventory not found for product ID: " + productId
                        ));

        return InventoryMapper.toResponse(inventory);
    }

    //Delete Inventory
    @Override
    public void deleteInventoryById(Long id) {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException(id));

        if (inventory.getReservedQuantity() > 0) {
            throw new InsufficientStockException(
                    "Cannot delete inventory with reserved stock."
            );
        }

        inventoryRepository.delete(inventory);
    }

    //Get All the inventories
    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {

        return inventoryRepository.findAll()
                .stream()
                .map(InventoryMapper::toResponse)
                .toList();
    }
}
