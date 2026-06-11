package com.example.event_ticketing.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.event_ticketing.services.EventService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.event_ticketing.dto.request.CreateEventRequest;
import com.example.event_ticketing.dto.response.ApiResponse;
import com.example.event_ticketing.dto.response.EventResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;


@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<Object> createEvent(@RequestBody @Valid CreateEventRequest request) {
        EventResponse event = eventService.createEvent(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<EventResponse>builder()
                        .success(true)
                        .message("Event berhasil dibuat")
                        .data(event)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<Object> getAllEvents() {
        List<EventResponse> events = eventService.getAllEvents();

        return ResponseEntity.ok(
                ApiResponse.<List<EventResponse>>builder()
                        .success(true)
                        .message("Events berhasil diambil")
                        .data(events)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEventById(@PathVariable UUID id) {
        EventResponse event = eventService.getEventById(id);

        return ResponseEntity.ok(
                ApiResponse.<EventResponse>builder()
                        .success(true)
                        .message("Event berhasil diambil")
                        .data(event)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateEvent(@PathVariable UUID id, @RequestBody @Valid CreateEventRequest request) {
        EventResponse eventUpdated = eventService.updateEvent(id, request);

        return ResponseEntity.ok(
                ApiResponse.<EventResponse>builder()
                        .success(true)
                        .message("Event berhasil diupdate")
                        .data(eventUpdated)
                        .build()
        );
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<Object> publishEvent(@PathVariable UUID id) {
        EventResponse eventPublished = eventService.publishEvent(id);

        return ResponseEntity.ok(
                ApiResponse.<EventResponse>builder()
                        .success(true)
                        .message("Event berhasil dipublikasikan")
                        .data(eventPublished)
                        .build()
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Object> cancelEvent(@PathVariable UUID id) {
        EventResponse eventPublished = eventService.cancelEvent(id);

        return ResponseEntity.ok(
                ApiResponse.<EventResponse>builder()
                        .success(true)
                        .message("Event berhasil dibatalkan")
                        .data(eventPublished)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Event berhasil dihapus")
                        .data(null)
                        .build()
        );
    }
}
