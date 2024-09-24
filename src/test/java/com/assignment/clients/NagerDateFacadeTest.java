package com.assignment.clients;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
class NagerDateFacadeTest {

    @Inject
    NagerDateClient nagerDateClient;

    @Inject
    NagerDateFacade nagerDateFacade;

    @Test
    void testUnknownCountryCode(){
        assertThrows(NagerException.class, () -> nagerDateFacade.getPublicHolidaysByCountryCode(2024, "INVALID"));
    }
}
