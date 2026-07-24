package com.amazonscale.inventory.controller;


import com.amazonscale.inventory.dto.InventoryRequest;
import com.amazonscale.inventory.dto.InventoryResponse;
import com.amazonscale.inventory.dto.InventoryUpdateRequest;
import com.amazonscale.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Inventories",
        description = "Inventory Management APIs"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    //Create Inventory
    @Operation(summary = "Create a new Inventory")
    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(
            @Valid @RequestBody final InventoryRequest inventoryRequest) {

        InventoryResponse inventoryResponse = inventoryService.createInventory(inventoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryResponse);
    }

    //Get Inventory by Id
    @Operation(summary = "Get Inventory by id")
    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(
            @PathVariable final Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    //Get all inventory
    @Operation(summary = "Get all inventories")
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    //Get inventory by productId
    @Operation(summary = "Get inventory by ProductId")
    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(
            @PathVariable final Long productId
    ){
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    //Updating inventory info
    @Operation(summary = "Updating Inventory Info using Id")
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable final Long id,
            @Valid @RequestBody final InventoryUpdateRequest inventoryUpdateRequest){

        return ResponseEntity.ok(inventoryService.updateInventory(id,inventoryUpdateRequest));
    }

    //Deleting inventory detail
    @Operation(summary = "Deleting Inventory by Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable final Long id) {
        inventoryService.deleteInventoryById(id);
        return ResponseEntity.noContent().build();
    }
}
