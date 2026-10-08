package com.reservation.vor.reservation_system;

import com.reservation.vor.reservation_system.service.StoreClosureService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StoreClosureTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreClosureService storeClosureService;

    @Test
    void publicCanGetClosureSettings() throws Exception {
        mockMvc.perform(get("/api/public/closure/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.regularClosedDays").isArray());
    }

    @Test
    void cannotBookOnClosedDay() throws Exception {
        // Ensure Sunday (0) is in regular closed days
        storeClosureService.setRegularClosedDays(List.of(0));

        // 2026-08-16 is a Sunday
        String sundayRequest = """
                {"name":"測試顧客","phone":"0988888888","reservationDate":"2026-08-16",
                 "reservationTime":"18:30","guestCount":2}
                """;

        mockMvc.perform(post("/api/public/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sundayRequest))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void adminCanManageClosureDates() throws Exception {
        // Login as admin
        String loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"VorCoffee","password":"20251210"}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = loginResult.replaceAll(".*\"accessToken\":\"([^\"]+)\".*", "$1");

        LocalDate adHocDate = LocalDate.of(2026, 10, 1);
        storeClosureService.removeClosedDate(adHocDate);

        // Add ad-hoc closure date
        mockMvc.perform(post("/api/store/closure/dates")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"2026-10-01","reason":"店休盤點"}
                                """))
                .andExpect(status().isCreated());

        // Try booking on that ad-hoc closure date -> should fail
        String adHocRequest = """
                {"name":"測試顧客","phone":"0977777777","reservationDate":"2026-10-01",
                 "reservationTime":"18:30","guestCount":2}
                """;
        mockMvc.perform(post("/api/public/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adHocRequest))
                .andExpect(status().is4xxClientError());

        // Remove the date
        storeClosureService.removeClosedDate(adHocDate);
    }
}
