package com.microservicesdemo.Inventory.Service.service;

import com.microservicesdemo.Inventory.Service.dto.InventoryResponse;
import com.microservicesdemo.Inventory.Service.model.Inventory;
import com.microservicesdemo.Inventory.Service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;


    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .map(inventory ->  mapToDto(inventory)).toList();
    }

    private InventoryResponse mapToDto(Inventory inventory) {
        return InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0).build();
    }
}
