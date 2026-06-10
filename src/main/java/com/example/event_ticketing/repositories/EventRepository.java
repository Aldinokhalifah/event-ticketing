package com.example.event_ticketing.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.event_ticketing.models.Event;

public interface EventRepository extends JpaRepository<Event, UUID>{
    
}
