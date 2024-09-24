package com.assignment.clients.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.Nullable;

@Serdeable
public record Country(
    @Nullable String countryCode,
    @Nullable String name
) {}
