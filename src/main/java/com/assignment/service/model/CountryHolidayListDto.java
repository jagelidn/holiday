package com.assignment.service.model;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record CountryHolidayListDto (
        String countryCode,
        List<HolidayDto> holidays
){}
