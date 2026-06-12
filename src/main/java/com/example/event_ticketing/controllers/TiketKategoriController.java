package com.example.event_ticketing.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.event_ticketing.dto.request.CreateTiketKategoriRequest;
import com.example.event_ticketing.dto.response.ApiResponse;
import com.example.event_ticketing.dto.response.TiketKategoriResponse;
import com.example.event_ticketing.services.TiketKategoriService;

import jakarta.validation.Valid;

@RestController
public class TiketKategoriController {
    private final TiketKategoriService tiketKategoriService;

    public TiketKategoriController(TiketKategoriService tiketKategoriService) {
        this.tiketKategoriService = tiketKategoriService;
    }


    @PostMapping("/api/events/{eventId}/tiket-kategori")
    public ResponseEntity<Object> createTiketKategori(@PathVariable UUID eventId, @RequestBody @Valid CreateTiketKategoriRequest request) { 
        TiketKategoriResponse tiketKategoriResponse = tiketKategoriService.createTiketKategori(eventId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.<TiketKategoriResponse>builder()
                .success(true)
                .message("Tiket berhasil dibuat")
                .data(tiketKategoriResponse)
                .build()
        );
    }

    @GetMapping("/api/events/{eventId}/tiket-kategori")
    public ResponseEntity<ApiResponse<List<TiketKategoriResponse>>> getTiketKategoriByEvent(@PathVariable UUID eventId) {
        List<TiketKategoriResponse> tiketKategoriResponse = tiketKategoriService.getTiketKategoriByEvent(eventId);

        ApiResponse<List<TiketKategoriResponse>> apiResponse = ApiResponse.<List<TiketKategoriResponse>>builder()
                .success(true)
                .message("Tiket kategori berhasil diambil")
                .data(tiketKategoriResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/api/tiket-kategori/{id}")
    public ResponseEntity<Object> updateTiketKategori(@PathVariable UUID id, @RequestBody @Valid CreateTiketKategoriRequest request) { 
        TiketKategoriResponse tiketKategoriUpdated = tiketKategoriService.updateTiketKategori(id, request);

        return ResponseEntity.ok(
                ApiResponse.<TiketKategoriResponse>builder()
                        .success(true)
                        .message("Tiket kategori berhasil diupdate")
                        .data(tiketKategoriUpdated)
                        .build()
        );
    }

    @DeleteMapping("/api/tiket-kategori/{id}")
    public ResponseEntity<Object> deleteTiketKategori(@PathVariable UUID id) { 
        tiketKategoriService.deleteTiketKategori(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Tiket berhasil dihapus")
                        .data(null)
                        .build()
        );
    }
}
