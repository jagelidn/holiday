package com.assignment.clients;

import com.assignment.clients.model.Country;
import com.assignment.clients.model.PublicHoliday;
import com.assignment.service.HolidayService;
import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.http.server.exceptions.NotFoundException;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Singleton
@CacheConfig("nager")
public class NagerDateFacade {
    private static final Logger logger = LoggerFactory.getLogger(NagerDateFacade.class);
    private final NagerDateClient nagerDateClient;

    public NagerDateFacade(NagerDateClient nagerDateClient){
        this.nagerDateClient = nagerDateClient;
    }

    public List<PublicHoliday> getPublicHolidaysByCountryName(int year, String countryName){
        var country = getCountry(countryName);

        if (country.isEmpty() || country.get().countryCode() == null){
            logger.info("Country unknown {}", countryName);
            throw new NoSuchElementException("Country unknown" + countryName);
        }

        return getPublicHolidaysByCountryCode(year, country.get().countryCode());
    }

    public List<PublicHoliday> getPublicHolidaysByCountryCode(int year, String countryCode){
        var response = nagerDateClient.getPublicHolidays(year, countryCode).join();
        return response.orElseThrow(() -> new NagerException("Empty response from NagerClient PublicHolidays. Possible 404 NOT FOUND, unsupported country code"));
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
