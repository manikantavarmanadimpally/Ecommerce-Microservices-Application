package com.microservicesdemo.order.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservicesdemo.order.service.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
