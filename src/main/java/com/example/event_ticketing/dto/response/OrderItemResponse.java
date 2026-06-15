package com.example.event_ticketing.dto.response;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItemResponse {
    private UUID id;
    private Integer jumlah;
    private BigDecimal hargaSatuan;
    private UUID tiketKategoriId;
}
