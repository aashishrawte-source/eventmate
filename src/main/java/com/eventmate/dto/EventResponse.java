package com.eventmate.dto;

import java.time.Instant;

public record EventResponse(
    Long id,
    String title,
    String description,
    String location,
    Instant startTime,
    Instant endTime,
    Integer capacity,
    Long createdBy,
    Instant createdAt
) {}
