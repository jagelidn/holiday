package com.assignment.service.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record HolidayDto (
        String name,
        String date
)
{}
