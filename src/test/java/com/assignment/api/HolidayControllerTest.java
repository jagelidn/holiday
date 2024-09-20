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
    void testVanilla(){
        var countryName = "Norway";

        given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get("/holiday/country/{countryName}/recent", Map.of("countryName", countryName))
                .then()
                .statusCode(200);
    }

    @Test
    void testVanilla2(){
        var countryName = "Norway";

        given().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .when()
                .get("/holiday/year/{year}/commonholidays/{countrycode1}/{countrycode2}",
                        Map.of("year", "2024", "countrycode1", "NO", "countrycode2", "SE"))
                .then()
                .statusCode(200);
    }


//Todo verify cache is working properly
    //todo verify illegalargument is converted to 400 bad request
}
