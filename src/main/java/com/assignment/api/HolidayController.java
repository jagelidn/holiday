package com.assignment.api;

import com.assignment.service.HolidayService;
import com.assignment.service.model.CountryHolidayListDto;
import com.assignment.service.model.DateHolidayListDto;
import com.assignment.service.model.HolidayDto;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("holiday")
public class HolidayController {
    private final HolidayService holidayService;
    private static final Logger logger = LoggerFactory.getLogger(HolidayController.class);

    public HolidayController(HolidayService holidayService){
        this.holidayService = holidayService;
    }

    @Error(exception = IllegalArgumentException.class)
    public HttpResponse<String> illegalArgument(IllegalArgumentException exception){
        return HttpResponse.badRequest(exception.getMessage());
    }

    @Error(exception = NoSuchElementException.class)
    public HttpResponse<String> illegalArgument(NoSuchElementException exception){
        return HttpResponse.notFound(exception.getMessage());
    }

    @Error(exception = Exception.class)
    public HttpResponse<String> unexpectedException(HttpRequest request, Exception exception){
        logger.error("Unexpected error processing request for holidays, path {}", request.getPath(), exception);
        return HttpResponse.serverError(exception.getMessage());
    }

    /**
     * Given a country name, returns the most recent celebrated 3 holidays (date and name).
     * @param countryName name of country
     * @return List of 3 most recent holidays, date and english name
     */
    @Get("/country/{countryName}/recent")
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<List<HolidayDto>> getRecentHolidays(final String countryName) {

        if (countryName == null || countryName.isBlank()){
            return HttpResponse.badRequest();
        }
        var resp = holidayService.getRecentHolidays(countryName);
        return HttpResponse.ok(resp);
    }

    /**
     * Given a year and country codes, for each country return a number of public holidays not falling on weekends.
     * Weekend is defined as saturday or sunday.
     * Descending order by date property.
     * @param year year under examination, current support for range inclusive (1974 - 2074)
     * @param countryCodes list of ISO-3166-2 country codes, empty list gives empty result
     * @return per country, public holidays not falling on weekends
     */
    @Get("/year/{year}/weekdayholidays")
    public HttpResponse<List<CountryHolidayListDto>> publicHolidaysWeekdays(final int year, @QueryValue List<String> countryCodes) {
        if (!isValidYear(year) || countryCodes.stream().anyMatch(c -> !isValidCountryCode(c))){
            return HttpResponse.badRequest();
        }
        return HttpResponse.ok(holidayService.getWeekdayHolidaysPerCountry(year, countryCodes));
    }

    /**
     * Given a year and 2 country codes, returns the deduplicated list of dates celebrated in both countries.
     * Country codes 1 & 2 cannot be the same.
     * @param year year under examination, current support for range inclusive (1974 - 2074)
     * @param countrycode1 ISO-3166-2 country code1
     * @param countrycode2 ISO-3166-2 country code2
     * @return List of dates celebrated in both countries and their local name variations
     */
    @Get("/year/{year}/commonholidays/{countrycode1}/{countrycode2}")
    public HttpResponse<List<DateHolidayListDto>> publicHolidaysWeekdays(final int year, final String countrycode1, final String countrycode2) {
        if(!isValidYear(year)){
            return HttpResponse.badRequest();
        }
        if (countrycode1.equals(countrycode2) || !isValidCountryCode(countrycode1) || !isValidCountryCode(countrycode2)){
            return HttpResponse.badRequest();
        }
        return HttpResponse.ok(holidayService.getCommonHolidays(year, countrycode1, countrycode2));
    }

    //Note: Nager service limitation. Likely dynamic 50 +- years. No proper documentation found from Nager reg support.
    private boolean isValidYear(int year){
        return year > 1973 && year < 2075;
    }

    private boolean isValidCountryCode(String countryCode){
        return Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2).contains(countryCode.toUpperCase());
    }


}
