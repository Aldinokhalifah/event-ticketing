package com.example.event_ticketing.repositories;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.event_ticketing.models.Tiket;

public interface TiketRepository extends JpaRepository<Tiket, UUID>{

    List<Tiket> findByUserId(UUID userId);
    
    Optional<Tiket> findByKodeTiket(String kodeTiket);
}
