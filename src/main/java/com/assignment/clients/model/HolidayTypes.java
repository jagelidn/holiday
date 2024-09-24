package com.assignment.clients.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public enum HolidayTypes {
    @JsonProperty("Public")
    PUBLIC,
    @JsonProperty("Bank")
    BANK,
    @JsonProperty("School")
    SCHOOL,
    @JsonProperty("Authorities")
    AUTHORITIES,
    @JsonProperty("Optional")
    OPTIONAL,
    @JsonProperty("Observance")
    OBSERVANCE
}
