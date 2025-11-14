package com.eventmate.service;

import com.eventmate.dto.EventRequest;
import com.eventmate.entity.Event;
import com.eventmate.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserService userService; // to validate organizer existence if needed

    @Transactional
    public Event createEvent(Long organizerId, EventRequest req) {
        validateTimes(req.startTime(), req.endTime());
        checkCapacity(req.capacity());
        checkOverlap(organizerId, req.startTime(), req.endTime(), null);

        Event event = Event.builder()
                .title(req.title())
                .description(req.description())
                .location(req.location())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .capacity(req.capacity())
                .createdBy(organizerId)
                .createdAt(Instant.now())
                .build();

        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(Long organizerId, Long eventId, EventRequest req) {
        validateTimes(req.startTime(), req.endTime());
        checkCapacity(req.capacity());

        Event existing = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!existing.getCreatedBy().equals(organizerId)) {
            throw new SecurityException("You are not the owner of this event");
        }

        checkOverlap(organizerId, req.startTime(), req.endTime(), eventId);

        existing.setTitle(req.title());
        existing.setDescription(req.description());
        existing.setLocation(req.location());
        existing.setStartTime(req.startTime());
        existing.setEndTime(req.endTime());
        existing.setCapacity(req.capacity());

        return eventRepository.save(existing);
    }

    @Transactional
    public void deleteEvent(Long organizerId, Long eventId) {
        Event existing = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!existing.getCreatedBy().equals(organizerId)) {
            throw new SecurityException("You are not the owner of this event");
        }
        eventRepository.delete(existing);
    }

    public Optional<Event> getEvent(Long id) {
        return eventRepository.findById(id);
    }

    private void validateTimes(Instant start, Instant end) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
    }

    private void checkCapacity(Integer capacity) {
        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
    }

    private void checkOverlap(Long organizerId, Instant start, Instant end, Long excludeId) {
        var overlaps = eventRepository.findOverlappingEvents(organizerId, start, end, excludeId);
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Event time overlaps with an existing event for this organizer");
        }
    }
}
