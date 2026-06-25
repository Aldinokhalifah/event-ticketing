package com.example.event_ticketing.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LaporanResponse {
    private UUID eventId;
    private String namaEvent;
    private Long totalTiketTerjual;
    private BigDecimal totalPendapatan;
    private List<KategoriSummaryResponse> rincianKategori;
}
