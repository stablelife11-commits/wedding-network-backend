package com.wedding.weddingnetwork.controller;

import com.wedding.weddingnetwork.dto.CreateEventRequest;
import com.wedding.weddingnetwork.dto.UpdateEventRequest;
import com.wedding.weddingnetwork.entity.Event;
import com.wedding.weddingnetwork.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody CreateEventRequest request) {
        Event event = eventService.createEvent(request);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<Event>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(eventService.getEvents(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateEventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }
}

