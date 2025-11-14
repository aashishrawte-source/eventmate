package com.eventmate.service;

import com.eventmate.dto.EventRequest;
import com.eventmate.entity.Event;
import com.eventmate.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private EventService eventService;

    private final Long organizerId = 1L;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEvent_ShouldSave_WhenValid() {
        EventRequest req = new EventRequest(
                "Event1", "desc", "Pune",
                Instant.parse("2025-12-01T10:00:00Z"),
                Instant.parse("2025-12-01T12:00:00Z"),
                50
        );

        when(eventRepository.findOverlappingEvents(any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event event = eventService.createEvent(organizerId, req);

        assertEquals(req.title(), event.getTitle());
        assertEquals(organizerId, event.getCreatedBy());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void createEvent_ShouldFail_WhenEndBeforeStart() {
        EventRequest req = new EventRequest(
                "Bad Event", "desc", "Pune",
                Instant.parse("2025-12-01T12:00:00Z"),
                Instant.parse("2025-12-01T10:00:00Z"),
                10
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                eventService.createEvent(organizerId, req));
        assertEquals("endTime must be after startTime", ex.getMessage());
    }

    @Test
    void createEvent_ShouldFail_WhenOverlaps() {
        EventRequest req = new EventRequest(
                "Overlap", "desc", "Pune",
                Instant.parse("2025-12-01T10:00:00Z"),
                Instant.parse("2025-12-01T12:00:00Z"),
                10
        );

        when(eventRepository.findOverlappingEvents(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(new Event()));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                eventService.createEvent(organizerId, req));
        assertTrue(ex.getMessage().contains("overlaps"));
    }

    @Test
    void updateEvent_ShouldFail_WhenNotOwner() {
        Event existing = Event.builder().id(5L).createdBy(99L).build();
        when(eventRepository.findById(5L)).thenReturn(Optional.of(existing));

        EventRequest req = new EventRequest(
                "Test", "desc", "Pune",
                Instant.parse("2025-12-01T10:00:00Z"),
                Instant.parse("2025-12-01T11:00:00Z"),
                5
        );

        SecurityException ex = assertThrows(SecurityException.class, () ->
                eventService.updateEvent(organizerId, 5L, req));
        assertEquals("You are not the owner of this event", ex.getMessage());
    }
}
