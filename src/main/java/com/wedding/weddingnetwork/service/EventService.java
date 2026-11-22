package com.wedding.weddingnetwork.service;

import com.wedding.weddingnetwork.dto.CreateEventRequest;
import com.wedding.weddingnetwork.dto.UpdateEventRequest;
import com.wedding.weddingnetwork.entity.Event;
import com.wedding.weddingnetwork.exception.ResourceNotFoundException;
import com.wedding.weddingnetwork.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(CreateEventRequest req) {
        Event event = Event.builder()
                .eventTitle(req.getEventTitle())
                .eventDate(req.getEventDate())
                .venueLocationId(req.getVenueLocationId())
                .cardTemplateUrl(req.getCardTemplateUrl())
                .createdAt(LocalDateTime.now())
                .build();
        return eventRepository.save(event);
    }

    public Page<Event> getEvents(int page, int size) {
        int validPage = Math.max(page, 0);
        int validSize = (size <= 0) ? 20 : Math.min(size, 100);
        Pageable pageable = PageRequest.of(validPage, validSize, Sort.by("eventDate").descending());
        return eventRepository.findAll(pageable);
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
    }

    public Event updateEvent(Long eventId, UpdateEventRequest req) {
        Event event = getEventById(eventId);
        event.setEventTitle(req.getEventTitle());
        event.setEventDate(req.getEventDate());
        event.setVenueLocationId(req.getVenueLocationId());
        event.setCardTemplateUrl(req.getCardTemplateUrl());
        return eventRepository.save(event);
    }
}

