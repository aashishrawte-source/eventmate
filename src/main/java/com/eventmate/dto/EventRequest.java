package com.eventmate.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record EventRequest(
    @NotBlank String title,
    String description,
    String location,
    @NotNull Instant startTime,
    @NotNull Instant endTime,
    @NotNull @Min(1) Integer capacity
) {}
