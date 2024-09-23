package com.assignment.clients;

import com.assignment.clients.model.Country;
import com.assignment.clients.model.PublicHoliday;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

@Singleton
@CacheConfig("nager")
public class NagerDateFacade {
    private final NagerDateClient nagerDateClient;

    public NagerDateFacade(NagerDateClient nagerDateClient){
        this.nagerDateClient = nagerDateClient;
    }

    public List<PublicHoliday> getPublicHolidaysByCountryName(int year, String countryName){
        var country = getCountry(countryName);

        if (country.isEmpty() || country.get().countryCode() == null){
            throw new IllegalArgumentException("Country unknown " + countryName);
        }

        return getPublicHolidaysByCountryCode(year, country.get().countryCode());
    }

    public List<PublicHoliday> getPublicHolidaysByCountryCode(int year, String countryCode){
        try {
            return nagerDateClient.getPublicHolidays(year, countryCode).join();
        }catch (HttpClientResponseException e){
            if(e.getStatus() == HttpStatus.NOT_FOUND) {
                throw new IllegalArgumentException("CountryCode is unknown ");
            }
            throw e;
        }
    }

    public Optional<Country> getCountry(String countryName){
        return getAvailableCountries().stream()
                .filter(c -> c.name().equals(countryName))
                .findFirst();
    }

    @Cacheable
    public List<Country> getAvailableCountries(){
        return nagerDateClient.getAvailableCountries().join();
    }

}
