package com.example.event_ticketing.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor 
public class KategoriSummaryResponse {
    private String nama;
    private Long terjual;
    private BigDecimal pendapatan;
}
