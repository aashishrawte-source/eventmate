package com.eventmate.controller;

import com.eventmate.dto.EventRequest;
import com.eventmate.dto.EventResponse;
import com.eventmate.entity.Event;
import com.eventmate.service.EventService;
import com.eventmate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserService userService; // optional usage

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            Authentication authentication,
            @Valid @RequestBody EventRequest req) {

        Long userId = extractUserId(authentication);
        Event created = eventService.createEvent(userId, req);
        EventResponse resp = toResponse(created);
        return ResponseEntity.created(URI.create("/api/events/" + created.getId())).body(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody EventRequest req) {

        Long userId = extractUserId(authentication);
        Event updated = eventService.updateEvent(userId, id, req);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            Authentication authentication,
            @PathVariable Long id) {

        Long userId = extractUserId(authentication);
        eventService.deleteEvent(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        return eventService.getEvent(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private EventResponse toResponse(Event e) {
        return new EventResponse(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getLocation(),
                e.getStartTime(),
                e.getEndTime(),
                e.getCapacity(),
                e.getCreatedBy(),
                e.getCreatedAt()
        );
    }

    // Extract user id from Authentication principal (we set principal to the user's id in the JWT filter)
    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) throw new SecurityException("Unauthenticated");
        Object principal = auth.getPrincipal();
        if (principal instanceof Long) return (Long) principal;
        // if principal is a UserDetails or other type, adapt as needed
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }
}
