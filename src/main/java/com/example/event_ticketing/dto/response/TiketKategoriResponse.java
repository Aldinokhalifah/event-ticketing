package com.example.event_ticketing.dto.response;

import java.math.BigDecimal;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TiketKategoriResponse  {
    private UUID id;
    private String nama;
    private BigDecimal harga;
    private Integer kuota;
    private Integer terjual;
    private UUID eventId;
}
