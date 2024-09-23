package com.assignment.service;

import com.assignment.clients.NagerDateFacade;
import com.assignment.clients.model.PublicHoliday;
import com.assignment.service.model.CountryHolidayListDto;
import com.assignment.service.model.DateHolidayListDto;
import com.assignment.service.model.HolidayDto;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class HolidayService {
    private static final Logger logger = LoggerFactory.getLogger(HolidayService.class);
    private final NagerDateFacade nagerDateFacade;

    public HolidayService(NagerDateFacade nagerDateFacade){
        this.nagerDateFacade = nagerDateFacade;
    }

    public List<HolidayDto> getRecentHolidays(String countryName){
        try {
            var year = LocalDate.now().getYear();
            var holidays = nagerDateFacade.getPublicHolidaysByCountryName(year, countryName);

            return holidays.stream()
                    .filter(publicHoliday -> LocalDate.parse(publicHoliday.date()).isBefore(LocalDate.now()))
                    .sorted((c1, c2) -> LocalDate.parse(c2.date()).compareTo(LocalDate.parse(c1.date())))
                    .limit(3)
                    .map(publicHoliday -> new HolidayDto(publicHoliday.name(), publicHoliday.date()))
                    .toList();

        }catch (DateTimeParseException e){
            logger.error("Failure to parse date of public holiday entity. [getRecentHolidays]");
            throw new IllegalStateException("Invalid date format(s)", e);
        }
    }

    public List<HolidayDto> getWeekdayHolidays(int year, String countryCode){
        var holidays = nagerDateFacade.getPublicHolidaysByCountryCode(year, countryCode);
        var weekendDays = List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

        try{
            return holidays.stream()
                    .filter(publicHoliday -> !weekendDays.contains(LocalDate.parse(publicHoliday.date()).getDayOfWeek()))
                    .map(publicHoliday -> new HolidayDto(publicHoliday.name(), publicHoliday.date()))
                    .sorted((c1, c2) -> LocalDate.parse(c2.date()).compareTo(LocalDate.parse(c1.date())))
                    .toList();

        }catch (DateTimeParseException e){
            logger.error("Failure to parse date of public holiday entity. [getWeekdayHolidays]");
            throw new IllegalStateException("Invalid date format(s)", e);
        }

    }

    public List<CountryHolidayListDto> getWeekdayHolidaysPerCountry(int year, List<String> countryCodes){
        return countryCodes.stream()
                .flatMap(country -> Stream.of(new CountryHolidayListDto(country, getWeekdayHolidays(year, country))))
                .toList();
    }


    public List<DateHolidayListDto> getCommonHolidays(int year, String countryCode1, String countryCode2){
        var holidaysC1 = nagerDateFacade.getPublicHolidaysByCountryCode(year, countryCode1);
        var holidaysC2 = nagerDateFacade.getPublicHolidaysByCountryCode(year, countryCode2);

        var holidays = new ArrayList<>(holidaysC1);
        holidays.addAll(holidaysC2);

        var commonHolidaysGroupedByDate = holidays.stream()
                .collect(Collectors.groupingBy(PublicHoliday::date))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1);

        return commonHolidaysGroupedByDate
                .map(entry -> new DateHolidayListDto(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(PublicHoliday::localName)
                                .filter(localName ->  localName != null && !localName.isEmpty())
                                .distinct().toList()))
                .toList();
    }
}
