package com.assignment.clients.model;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.Nullable;

import java.util.List;

@Serdeable
public record PublicHoliday (
  String date,
  @Nullable String localName,
  @Nullable String name,
  @Nullable String countryCode,
  boolean global,
  @Nullable List<String> counties, //ISO-3166-2
  @Nullable int launchYear,
  @Nullable List<HolidayTypes> types
){ }
