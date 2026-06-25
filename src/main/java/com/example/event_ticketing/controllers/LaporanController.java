package com.example.event_ticketing.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.example.event_ticketing.services.LaporanService;
import com.example.event_ticketing.dto.response.ApiResponse;
import com.example.event_ticketing.dto.response.LaporanResponse;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@RestController
@RequestMapping("/api/laporan")
public class LaporanController {
    private final LaporanService laporanService;

    public LaporanController(LaporanService laporanService) {
        this.laporanService = laporanService;
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<Object> laporanSummary(@PathVariable UUID eventId) {
        LaporanResponse summary = laporanService.getSalesSummary(eventId);

        return ResponseEntity.ok(
                ApiResponse.<LaporanResponse>builder()
                        .success(true)
                        .message("Laporan summary event berhasil diambil")
                        .data(summary)
                        .build()
        );
    }
}