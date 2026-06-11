package com.example.event_ticketing.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.example.event_ticketing.models.enums.EventStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class EventResponse {
    private UUID id;
    private String nama;
    private String deskripsi;
    private String lokasi;
    private LocalDate tanggal;
    private LocalTime waktuMulai;
    private LocalTime waktuSelesai;
    private EventStatus status;
}