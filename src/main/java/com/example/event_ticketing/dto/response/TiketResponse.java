package com.example.event_ticketing.dto.response;

import java.util.UUID;

import com.example.event_ticketing.models.enums.TiketStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TiketResponse {
    private UUID id;
    private String kodeTiket;
    private TiketStatus status;
    private UUID userId;
    private UUID orderItemId;
}
