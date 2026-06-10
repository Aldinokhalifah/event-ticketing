package com.example.event_ticketing.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.event_ticketing.models.Order;

public interface OrderRepository extends JpaRepository<Order, UUID>{
    
    List<Order> findByUserId(UUID userId);

    Optional<Order> findByKodeOrder(String kodeOrder);
}
