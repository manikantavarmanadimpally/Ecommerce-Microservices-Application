package com.microservicesdemo.order.service.service;

import com.microservicesdemo.order.service.dto.InventoryResponse;
import com.microservicesdemo.order.service.dto.OrderLineItemsDto;
import com.microservicesdemo.order.service.dto.OrderRequest;
import com.microservicesdemo.order.service.model.Order;
import com.microservicesdemo.order.service.model.OrderLineItems;
import com.microservicesdemo.order.service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    @Autowired
    DiscoveryClient discoveryClient;

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItemsList(orderRequest.getOrderLineItemsDtoList().stream().map(this::mapToDto).toList());
        //Call Inventory Service and place order if product is in stock
        List<String> skuCodes= order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();
        List<String> services = discoveryClient.getServices().stream()
                .flatMap(serviceId -> discoveryClient.getInstances(serviceId).stream())
                .map(instance -> instance.getServiceId() + ": " + instance.getHost() + ":" + instance.getPort())
                .toList();
        services.forEach(System.out::println);
        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://INVENTORY-SERVICE/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCodes",skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock");
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

        return orderLineItems;
    }
}
