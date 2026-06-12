package com.example.event_ticketing.repositories;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.example.event_ticketing.models.TiketKategori;

import jakarta.persistence.LockModeType;

public interface TiketKategoriRepository extends JpaRepository<TiketKategori, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TiketKategori t WHERE t.id = :id")
    TiketKategori findByIdWithLock(UUID id);

    List<TiketKategori> findByEventId(UUID eventId);
}
