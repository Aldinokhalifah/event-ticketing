package com.example.event_ticketing.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.event_ticketing.dto.request.CreateEventRequest;
import com.example.event_ticketing.dto.response.EventResponse;
import com.example.event_ticketing.models.Event;
import com.example.event_ticketing.models.enums.EventStatus;
import com.example.event_ticketing.repositories.EventRepository;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    private EventResponse toResponse(Event event) {
        return EventResponse.builder()
            .id(event.getId())
            .nama(event.getNama())
            .deskripsi(event.getDeskripsi())
            .lokasi(event.getLokasi())
            .tanggal(event.getTanggal())
            .waktuMulai(event.getWaktuMulai())
            .waktuSelesai(event.getWaktuSelesai())
            .status(event.getStatus())
            .build();
    }

    public EventResponse createEvent(CreateEventRequest request) {
        Event event = Event.builder()
            .nama(request.getNama())
            .deskripsi(request.getDeskripsi())
            .lokasi(request.getLokasi())
            .tanggal(request.getTanggal())
            .waktuMulai(request.getWaktuMulai())
            .waktuSelesai(request.getWaktuSelesai())
            .build();

        Event saved = eventRepository.save(event);

        return toResponse(saved);
    }

    public List<EventResponse> getAllEvents() {
        List<Event> events = eventRepository.findAll();

        List<EventResponse> responses = events.stream().map(this::toResponse).collect(Collectors.toList());

        return responses;
    }

    public EventResponse getEventById(UUID id) {
        Event event = eventRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Event tidak ditemukan"));

        return toResponse(event);
    }

    public EventResponse updateEvent(UUID id, CreateEventRequest request) {
        Event event = eventRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Event tidak ditemukan"));

        if(event.getStatus() != EventStatus.DRAFT) {
            throw new IllegalStateException("Event sudah dipublikasi atau dibatalkan");
        } else {
            event.setNama(request.getNama());
            event.setDeskripsi(request.getDeskripsi());
            event.setTanggal(request.getTanggal());
            event.setLokasi(request.getLokasi());
            event.setWaktuMulai(request.getWaktuMulai());
            event.setWaktuSelesai(request.getWaktuSelesai());

            Event saved = eventRepository.save(event);
            return toResponse(saved);
        }
    }

    public EventResponse publishEvent(UUID id) {
        Event event = eventRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Event tidak ditemukan"));

        if(event.getStatus() != EventStatus.DRAFT) {
            throw new IllegalStateException("Event sudah dipublikasi atau dibatalkan");
        } else {
            event.setStatus(EventStatus.PUBLISHED);

            Event saved = eventRepository.save(event);
            return toResponse(saved);
        }
    }

    public EventResponse cancelEvent(UUID id) {
        Event event = eventRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Event tidak ditemukan"));

        if(event.getStatus() == EventStatus.CANCELLED) {
            throw new IllegalStateException("Tidak dapat membatalkan event dua kali");
        } else {
            event.setStatus(EventStatus.CANCELLED);

            Event saved = eventRepository.save(event);
            return toResponse(saved);
        }
    }

    public void deleteEvent(UUID id) {
        Event event = eventRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Event tidak ditemukan"));

        if(event.getStatus() != EventStatus.DRAFT) {
            throw new IllegalStateException("Tidak dapat menghapus event jika bukan draft");
        }

        eventRepository.delete(event);
    }
}
