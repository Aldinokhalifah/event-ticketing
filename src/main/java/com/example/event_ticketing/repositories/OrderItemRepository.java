package com.example.event_ticketing.repositories;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.event_ticketing.models.OrderItem;
import com.example.event_ticketing.dto.response.OrderItemResponse;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID>{
    List<OrderItem> findByOrderId(UUID orderId);
}
