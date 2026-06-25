package com.example.event_ticketing.services;

import java.util.UUID;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.event_ticketing.dto.response.LaporanResponse;
import com.example.event_ticketing.dto.response.KategoriSummaryResponse;
import com.example.event_ticketing.models.Event;
import com.example.event_ticketing.repositories.EventRepository;
import com.example.event_ticketing.repositories.OrderItemRepository;

@Service
public class LaporanService {
    private final EventRepository eventRepository;
    private final OrderItemRepository orderItemRepository;

    public LaporanService(EventRepository eventRepository, OrderItemRepository orderItemRepository) {
        this.eventRepository = eventRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public LaporanResponse getSalesSummary(UUID eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event tidak ditemukan"));

        List<KategoriSummaryResponse> rincianKategori = orderItemRepository.getSalesSummaryByEvent(eventId);

        long totalTiketTerjual = rincianKategori.stream()
        .mapToLong(KategoriSummaryResponse::getTerjual)
        .sum();

        BigDecimal totalPendapatan = rincianKategori.stream()
        .map(KategoriSummaryResponse::getPendapatan)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

        LaporanResponse laporanResponse = LaporanResponse.builder()
            .eventId(eventId)
            .namaEvent(event.getNama())
            .totalTiketTerjual(totalTiketTerjual)
            .totalPendapatan(totalPendapatan)
            .rincianKategori(rincianKategori)
            .build();

        return laporanResponse;
    }
}
