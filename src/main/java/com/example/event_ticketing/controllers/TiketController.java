package com.example.event_ticketing.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.event_ticketing.dto.response.ApiResponse;
import com.example.event_ticketing.dto.response.TiketResponse;
import com.example.event_ticketing.models.User;
import com.example.event_ticketing.services.TiketService;


@RestController
@RequestMapping("/api/tikets")
public class TiketController {
    private final TiketService tiketService;

    public TiketController(TiketService tiketService) {
        this.tiketService = tiketService;
    }

    @GetMapping
    public ResponseEntity<Object> getUserTikets(@AuthenticationPrincipal User currentUser) {
        List<TiketResponse> userTikets = tiketService.getUserTikets(currentUser);

        return ResponseEntity.ok(
            ApiResponse.<List<TiketResponse>>builder()
            .success(true)
            .message("Tiket milik user berhasil diambil")
            .data(userTikets)
            .build()
        );
    }
    
    @PatchMapping("/{id}/checkin")
    public ResponseEntity<Object> checkinTiket(@PathVariable UUID id) {
        TiketResponse tiket = tiketService.checkinTiket(id);

        return ResponseEntity.ok(
            ApiResponse.<TiketResponse>builder()
            .success(true)
            .message("Tiket berhasil dicheck-in")
            .data(tiket)
            .build()
        );
    }
}
