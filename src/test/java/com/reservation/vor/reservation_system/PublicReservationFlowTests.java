package com.reservation.vor.reservation_system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PublicReservationFlowTests {
    @Autowired
    private MockMvc mockMvc;

    private static final String REQUEST = """
            {"name":"王小明","phone":"0912345678","reservationDate":"2026-08-15",
             "reservationTime":"18:30","guestCount":2,"notes":"靠窗座位","recommendedBy":"小美"}
            """;

    @Test
    void customerCanCreateViewUpdateAndCancelReservation() throws Exception {
        String result = mockMvc.perform(post("/api/public/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("王小明"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        String token = result.replaceAll(".*\\\"publicToken\\\":\\\"([^\\\"]+)\\\".*", "$1");
        mockMvc.perform(get("/api/public/reservations/{token}", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerPhone").value("0912345678"));

        mockMvc.perform(patch("/api/public/reservations/{token}", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REQUEST.replace("18:30", "19:00")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationTime").value("19:00:00"));

        mockMvc.perform(post("/api/public/reservations/{token}/cancel", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void bookingWebsiteIsPubliclyAccessible() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
