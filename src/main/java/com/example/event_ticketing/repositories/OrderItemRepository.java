package com.example.event_ticketing.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.event_ticketing.models.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID>{
    
}
