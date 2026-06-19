package com.example.event_ticketing.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.event_ticketing.dto.request.CreateOrderRequest;
import com.example.event_ticketing.dto.response.ApiResponse;
import com.example.event_ticketing.dto.response.OrderResponse;
import com.example.event_ticketing.models.User;
import com.example.event_ticketing.services.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestBody @Valid CreateOrderRequest request, @AuthenticationPrincipal User currentUser) {

        OrderResponse order = orderService.createOrder(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order berhasil dibuat")
                .data(order)
                .build()
        );
    }

    @GetMapping
    public ResponseEntity<Object> getUserOrders( @AuthenticationPrincipal User currentUser) {
        List<OrderResponse> orders = orderService.getUserOrders(currentUser);

        return ResponseEntity.ok(
            ApiResponse.<List<OrderResponse>>builder()
            .success(true)
            .message("Order milik user berhasil diambil")
            .data(orders)
            .build()
        );
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        OrderResponse order = orderService.getOrderById(id, currentUser);

        return ResponseEntity.ok(
            ApiResponse.<OrderResponse>builder()
            .success(true)
            .message("Order berhasil diambil")
            .data(order)
            .build()
        );
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<Object> payOrder(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        OrderResponse orderPaid = orderService.payOrder(id, currentUser);

        return ResponseEntity.ok(
            ApiResponse.<OrderResponse>builder()
            .success(true)
            .message("Order berhasil dibayarkan")
            .data(orderPaid)
            .build()
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Object> cancelOrder(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        OrderResponse orderCancelled = orderService.cancelOrder(id, currentUser);

        return ResponseEntity.ok(
            ApiResponse.<OrderResponse>builder()
            .success(true)
            .message("Order berhasil dibatalkan")
            .data(orderCancelled)
            .build()
        );
    }
    


}
