package com.assignment.api;

import com.assignment.service.HolidayService;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;


@MicronautTest
class HolidayControllerTest {

    @Inject
    EmbeddedServer server;

    @Inject
    HolidayService holidayService;

    @BeforeEach
    public void setup() {
        RestAssured.port = server.getPort();
    }

    @Test
    void testRecentCelebrations(){
        var countryName = "Norway";

        given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get("/holiday/country/{countryName}/recent", Map.of("countryName", countryName))
                .then()
                .statusCode(200);

        //Todo verify output body
    }

    @Test
    void testUnknownCountryName(){
        var countryName = "Not A Country";

        given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get("/holiday/country/{countryName}/recent", Map.of("countryName", countryName))
                .then()
                .statusCode(400);
    }

    @Test
    void testCommonHolidays(){
        given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get("/holiday/year/{year}/commonholidays/{countrycode1}/{countrycode2}",
                        Map.of("year", "2024", "countrycode1", "NO", "countrycode2", "SE"))
                .then()
                .statusCode(200);

        //Todo verify output body
    }

    @Test
    void testWeekdayHolidays(){
        given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .param("countryCodes", List.of("NL", "SE"))
                .when()
                .get("/holiday/year/{year}/weekdayholidays",
                        Map.of("year", "2023"))
                .then()
                .statusCode(200);

        //Todo verify output body
    }


    @ParameterizedTest
    @ValueSource(strings = {"INVALID", " ", "   "})
    void testInvalidCountryCode(String cc1){

        given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get("/holiday/year/{year}/commonholidays/{countrycode1}/{countrycode2}",
                        Map.of("year", "2024", "countrycode1", cc1, "countrycode2", "SE"))
                .then()
                .statusCode(400);
    }


    @ParameterizedTest
    @ValueSource(ints = {1901, 2092})
    void testInvalidYear(int year){

        given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get("/holiday/year/{year}/commonholidays/{countrycode1}/{countrycode2}",
                        Map.of("year", year, "countrycode1", "NO", "countrycode2", "SE"))
                .then()
                .statusCode(400);
    }

}
