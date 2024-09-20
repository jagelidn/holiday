package com.assignment.service.model;

import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Serdeable
public record DateHolidayListDto (
        String date,
        List<String> localNames
){
}
