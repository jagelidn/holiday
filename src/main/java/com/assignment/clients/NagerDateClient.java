package com.assignment.clients;

import com.assignment.clients.model.Country;
import com.assignment.clients.model.PublicHoliday;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Client("${nager.host}")
public interface NagerDateClient {

    @Get("/api/v3/AvailableCountries")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CompletableFuture<List<Country>> getAvailableCountries();

    @Get("/api/v3/PublicHolidays/{year}/{countryCode}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CompletableFuture<Optional<List<PublicHoliday>>> getPublicHolidays(
            @PathVariable int year,
            @PathVariable String countryCode
    );

}
