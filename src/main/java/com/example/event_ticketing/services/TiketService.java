package com.example.event_ticketing.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import com.example.event_ticketing.dto.response.TiketResponse;
import com.example.event_ticketing.models.Tiket;
import com.example.event_ticketing.models.User;
import com.example.event_ticketing.models.enums.TiketStatus;
import com.example.event_ticketing.repositories.TiketRepository;

@Service
public class TiketService {
    private final TiketRepository tiketRepository;

    public TiketService(TiketRepository tiketRepository) {
        this.tiketRepository = tiketRepository;
    }

    private TiketResponse toResponse(Tiket tiket) {
        return TiketResponse.builder()
            .id(tiket.getId())
            .kodeTiket(tiket.getKodeTiket())
            .status(tiket.getStatus())
            .userId(tiket.getUser().getId())
            .orderItemId(tiket.getOrderItem().getId())
            .build();
    }

    public List<TiketResponse> getUserTikets(User currentUser) {
        List<Tiket> userTikets = tiketRepository.findByUserId(currentUser.getId());

        return userTikets.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TiketResponse checkinTiket(UUID id) {
        Tiket tiket = tiketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tiket tidak ditemukan"));

        switch (tiket.getStatus()) {
            case USED -> throw new IllegalStateException("Tiket sudah pernah check-in");
            case CANCELLED -> throw new IllegalStateException("Tiket sudah dibatalkan, tidak bisa check-in");
            case ACTIVE -> {
                tiket.setStatus(TiketStatus.USED);
                tiketRepository.save(tiket);
            }
        }

        return toResponse(tiket);
    }
}
