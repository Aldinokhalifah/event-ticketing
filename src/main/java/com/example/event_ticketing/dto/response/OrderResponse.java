package com.example.event_ticketing.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.List;

import com.example.event_ticketing.models.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private UUID id;
    private String kodeOrder;
    private LocalDateTime tanggalOrder;
    private BigDecimal totalHarga;
    private OrderStatus status;
    private UUID user;
    private List<OrderItemResponse> items;
}
