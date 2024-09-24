package com.assignment.service;

import com.assignment.clients.NagerDateFacade;
import com.assignment.clients.model.HolidayTypes;
import com.assignment.clients.model.PublicHoliday;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@MicronautTest
class HolidayServiceTest {

    @Mock
    NagerDateFacade nagerDateFacade;

    HolidayService holidayService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        holidayService = new HolidayService(nagerDateFacade);
    }

    @Test
    void testGetRecentHolidays(){
        var country = "Germany";

        when(nagerDateFacade.getPublicHolidaysByCountryName(anyInt(), eq(country))).thenReturn(List.of(
                new PublicHoliday("2024-08-15", null, "Assumption Day", "DE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-05-30", null, "Corpus Christi", "DE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-05-01", null, "Labour Day", "DE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-03-31", null, "Easter Sunday", "DE",  false, null, null, List.of(HolidayTypes.PUBLIC))
        ));

        var result = holidayService.getRecentHolidays(country);

        assertEquals(3, result.size());
        assertEquals("Assumption Day", result.get(0).name());
        assertEquals("2024-08-15", result.get(0).date());
        assertEquals("Corpus Christi", result.get(1).name());
        assertEquals("2024-05-30", result.get(1).date());
        assertEquals("Labour Day", result.get(2).name());
        assertEquals("2024-05-01", result.get(2).date());

    }

    @Test
    void testGetRecentHolidaysDateTimeParseException(){
        var country = "Germany";

        when(nagerDateFacade.getPublicHolidaysByCountryName(2024, country)).thenReturn(List.of(
                new PublicHoliday("2024-08-15", null, "Assumption Day", "DE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("not a date", null, "Labour Day", "DE",  false, null, null, List.of(HolidayTypes.PUBLIC))
        ));

        assertThrows(IllegalStateException.class, () -> holidayService.getRecentHolidays(country));

    }

    @Test
    void testGetCommonHolidays(){
        var countryCode1 = "DE";
        var countryCode2 = "SE";

        when(nagerDateFacade.getPublicHolidaysByCountryCode(2024, countryCode1)).thenReturn(List.of(
                new PublicHoliday("2024-08-15", null, "Assumption Day", "DE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-12-26", "Zweiter Weihnachtstag", "St. Stephen's Day", "DE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-05-01", "Tag der Arbeit", "Labour Day", "DE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-03-31", null, "Easter Sunday", "DE",  false, null, null, List.of(HolidayTypes.PUBLIC))
        ));

        when(nagerDateFacade.getPublicHolidaysByCountryCode(2024, countryCode2)).thenReturn(List.of(
                new PublicHoliday("2024-05-01", "Första maj", "Labour Day", "SE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-12-26", "Annandag jul", "St. Stephen's Day", "SE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-12-31", "Nyårsafton", "New Year's Eve", "SE",  false, null, null, List.of(HolidayTypes.PUBLIC))
        ));

        var result = holidayService.getCommonHolidays(2024, countryCode1, countryCode2);

        assertEquals(2, result.size());
        assertEquals("2024-12-26", result.get(0).date());
        assertEquals("2024-05-01", result.get(1).date());

        assertThat(result.get(0).localNames()).hasSameElementsAs(List.of("Zweiter Weihnachtstag", "Annandag jul"));
        assertThat(result.get(1).localNames()).hasSameElementsAs(List.of("Tag der Arbeit", "Första maj"));

    }

    @Test
    void testGetWeekdayHolidays(){
        var countryCode = "SE";

        when(nagerDateFacade.getPublicHolidaysByCountryCode(2024, countryCode)).thenReturn(List.of(
                new PublicHoliday("2024-05-01", "Första maj", "Labour Day", "SE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-12-31", "Nyårsafton", "New Year's Eve", "SE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-06-22", "Midsommardagen", "Midsummer Day", "SE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-05-19", "Pingstdagen", "Pentecost", "SE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-12-26", "Annandag jul", "St. Stephen's Day", "SE",  false, null, null, List.of(HolidayTypes.PUBLIC))

        ));
        var result = holidayService.getWeekdayHolidays(2024, countryCode);

        assertEquals(3, result.size());
        assertEquals("New Year's Eve", result.get(0).name());
        assertEquals("St. Stephen's Day", result.get(1).name());
        assertEquals("Labour Day", result.get(2).name());
    }

    @Test
    void testGetWeekdayHolidaysEmptyList(){

        var result = holidayService.getWeekdayHolidaysPerCountry(2024, Collections.emptyList());
        assertEquals(0, result.size());
    }

    @Test
    void testGetWeekdayHolidaysMixedValidityCountryCodes(){
        var countryCode = "SE";
        var invalidCountryCode = "INVALID";
        var countryCpdes = List.of(countryCode, invalidCountryCode);

        doThrow(new IllegalArgumentException("CountryCode is unknown ")).when(nagerDateFacade).getPublicHolidaysByCountryCode(2024, invalidCountryCode);
        when(nagerDateFacade.getPublicHolidaysByCountryCode(2024, countryCode)).thenReturn(List.of(
                new PublicHoliday("2024-05-01", "Första maj", "Labour Day", "SE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-06-22", "Midsommardagen", "Midsummer Day", "SE",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("2024-12-26", "Annandag jul", "St. Stephen's Day", "SE",  false, null, null, List.of(HolidayTypes.PUBLIC))

        ));

        assertThrows(IllegalArgumentException.class, () -> holidayService.getWeekdayHolidaysPerCountry(2024,countryCpdes));

    }

    @Test
    void testGetWeekdayHolidaysDateTimeParseException(){

        when(nagerDateFacade.getPublicHolidaysByCountryCode(2024, "NL")).thenReturn(List.of(
                new PublicHoliday("2024-08-15", null, "Assumption Day", "NL",  false, null, null, List.of(HolidayTypes.PUBLIC)),
                new PublicHoliday("not a date", null, "Labour Day", "NL",  false, null, null, List.of(HolidayTypes.PUBLIC))
        ));

        assertThrows(IllegalStateException.class, () -> holidayService.getWeekdayHolidays(2024, "NL"));

    }
}
