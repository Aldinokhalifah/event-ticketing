package com.example.event_ticketing.services;

import java.util.UUID;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.event_ticketing.dto.request.CreateTiketKategoriRequest;
import com.example.event_ticketing.dto.response.TiketKategoriResponse;
import com.example.event_ticketing.models.Event;
import com.example.event_ticketing.models.TiketKategori;
import com.example.event_ticketing.models.enums.EventStatus;
import com.example.event_ticketing.repositories.EventRepository;
import com.example.event_ticketing.repositories.TiketKategoriRepository;

@Service
public class TiketKategoriService {
    private final TiketKategoriRepository tiketKategoriRepository;
    private final EventRepository eventRepository;

    public TiketKategoriService(TiketKategoriRepository tiketKategoriRepository,EventRepository eventRepository) {
        this.tiketKategoriRepository = tiketKategoriRepository;
        this.eventRepository = eventRepository;
    }

    private TiketKategoriResponse toResponse(TiketKategori tiketKategori) {
        return TiketKategoriResponse.builder()
            .id(tiketKategori.getId())
            .nama(tiketKategori.getNama())
            .harga(tiketKategori.getHarga())
            .kuota(tiketKategori.getKuota())
            .terjual(tiketKategori.getTerjual())
            .eventId(tiketKategori.getEvent().getId()) 
            .build();
    }

    public TiketKategoriResponse createTiketKategori(UUID eventId, CreateTiketKategoriRequest request) {
        Event event = eventRepository.findById(eventId).orElseThrow( () -> new RuntimeException("Event tidak ditemukan"));

        if(event.getStatus() != EventStatus.PUBLISHED) {
            throw new IllegalStateException("Tiket kategori hanya bisa ditambahkan ke event yang sudah dipublikasikan");
        }

        TiketKategori tiketKategori = TiketKategori.builder()
            .nama(request.getNama())
            .harga(request.getHarga())
            .kuota(request.getKuota())
            .event(event)
            .terjual(0)
            .build();

        TiketKategori saved = tiketKategoriRepository.save(tiketKategori);
        return toResponse(saved); 
    }

    public List<TiketKategoriResponse> getTiketKategoriByEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event tidak ditemukan"));

        List<TiketKategori> tiketKategori = tiketKategoriRepository.findByEventId(event.getId());

        return tiketKategori.stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public TiketKategoriResponse updateTiketKategori(UUID id, CreateTiketKategoriRequest request) {
        TiketKategori tiketKategori = tiketKategoriRepository.findById(id).orElseThrow(() -> new RuntimeException("Tiket Kategori tidak ditemukan"));

        tiketKategori.setNama(request.getNama());
        tiketKategori.setHarga(request.getHarga());
        tiketKategori.setKuota(request.getKuota());

        TiketKategori saved = tiketKategoriRepository.save(tiketKategori);
        return toResponse(saved); 
    }

    public void deleteTiketKategori(UUID id) {
        TiketKategori tiketKategori = tiketKategoriRepository.findById(id).orElseThrow(() -> new RuntimeException("Tiket Kategori tidak ditemukan"));

        tiketKategoriRepository.delete(tiketKategori);
    }
}
