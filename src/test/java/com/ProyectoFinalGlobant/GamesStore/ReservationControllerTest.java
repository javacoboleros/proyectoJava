package com.ProyectoFinalGlobant.GamesStore;

import com.ProyectoFinalGlobant.GamesStore.controllers.ReservationController;
import com.ProyectoFinalGlobant.GamesStore.models.ReservationModel;
import com.ProyectoFinalGlobant.GamesStore.services.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    private ReservationModel reservation1 = new ReservationModel(1L, 1L, "Juan", "Perez", "mail1@mail.com", "0001");
    private ReservationModel reservation2 = new ReservationModel( 2L, 1L, "John", "Doe", "mail2@mail.com", "0002");
    private ArrayList<ReservationModel> reservationList;

    @BeforeEach
    void fillList() {
        reservationList = new ArrayList<ReservationModel>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);
    }

    @Test
    void shouldGetEmptyReservations() throws Exception {
        given(reservationService.getReservations()).willReturn( new ArrayList<ReservationModel>());
        mockMvc.perform(get("/reservation"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldGetAllReservations() throws Exception {
        given(reservationService.getReservations()).willReturn(reservationList);
        mockMvc.perform(get("/reservation"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldGetOneReservationByID() throws Exception {
        long reservationId = reservation1.getId();
        given(reservationService.getReservationById(reservation1.getId())).willReturn(java.util.Optional.ofNullable(reservation1));
        mockMvc.perform(get("/reservation/{reservationId}", reservationId))
                .andExpect(jsonPath("$.name", is(reservation1.getName())));
    }

    @Test
    void shouldGetReservationByIdReservationNotFoundException () throws Exception {
        long reservationId = reservation1.getId();
        given(reservationService.getReservationById(reservation1.getId())).willReturn(Optional.empty());
        mockMvc.perform(get("/reservation/{reservationId}", reservationId))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Reservation " + reservationId + " does not exist")));
    }

    @Test
    void shouldGetAllReservationsByGameId() throws Exception {
        long gameId = reservation1.getGameId();
        given(reservationService.getReservationByGameId(gameId)).willReturn(reservationList);
        mockMvc.perform(get("/reservation/game/{gameId}", gameId))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldGetReservationByGameIdReservationNotFoundException () throws Exception {
        long gameId = reservation1.getGameId();
        given(reservationService.getReservationByGameId(reservation1.getGameId())).willReturn(new ArrayList<ReservationModel>());
        mockMvc.perform(get("/reservation/game/{gameId}", gameId))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("There is no reservations for game " + gameId)));
    }

    @Test
    void shouldCreateNewReservation() throws Exception {
        given(reservationService.saveReservation(any())).willReturn(reservation1);
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"gameId\":1, \"name\":\"Juan\", \"lastName\":\"Perez\", \"documentNumber\":\"0001\", \"email\":\"mail1@mail.com\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Juan")));
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"gameId\":1, \"name\":\"Juan\", \"lastName\":\"Perez\", \"documentNumber\":\"0001\", \"email\":\"MAIL1@MAIL.COM\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Juan")));
    }

    @Test
    void shouldCreateNewReservationReservationBadRequestExceptionFields() throws Exception {
        given(reservationService.saveReservation(any())).willReturn(reservation1);
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"gameId\":1, \"lastName\":\"Perez\", \"documentNumber\":\"0001\", \"email\":\"mail1@mail.com\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Empty field on creation request")));
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"gameId\":0, \"name\":\"Juan\", \"lastName\":\"Perez\", \"documentNumber\":\"0001\", \"email\":\"mail1@mail.com\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Empty field on creation request")));
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\":\"Juan\", \"lastName\":\"Perez\", \"documentNumber\":\"0001\", \"email\":\"mail1@mail.com\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Empty field on creation request")));
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"gameId\":1, \"name\": \"Juan\", \"documentNumber\":\"0001\", \"email\":\"mail1@mail.com\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Empty field on creation request")));
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"gameId\":1, \"name\": \"Juan\", \"lastName\":\"Perez\", \"email\":\"mail1@mail.com\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Empty field on creation request")));
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"gameId\":1, \"name\": \"Juan\", \"lastName\":\"Perez\", \"documentNumber\":\"0001\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Empty field on creation request")));
    }

    @Test
    void shouldCreateNewReservationReservationBadRequestExceptionEmail() throws Exception {
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"gameId\":1, \"name\":\"Juan\", \"lastName\":\"Perez\", \"documentNumber\":\"0001\", \"email\":\"mail1mail.com\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Invalid email on creation request")));
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"gameId\":1, \"name\":\"Juan\", \"lastName\":\"Perez\", \"documentNumber\":\"0001\", \"email\":\"mail1@mail\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Invalid email on creation request")));
    }

    @Test
    void shouldUpdateReservation() throws Exception {
        long reservationId = reservation1.getId();
        given(reservationService.updateReservation(any(), any())).willReturn(reservation1);
        mockMvc.perform(put("/reservation/{reservationId}", reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"gameId\":1, \"name\":\"Juan\", \"lastName\":\"Perez\", \"documentNumber\":\"0001\", \"email\":\"mail1@mail.com\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Juan")));
    }

    @Test
    void shouldUpdateReservationReservationBadRequestExceptionFields() throws Exception {
        long reservationId = reservation1.getId();
        mockMvc.perform(put("/reservation/{reservationId}", reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\":\"Juan\", \"email\":\"mail1@mail.com\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Empty field on update request")));
        //"gameId":1,
    }

    @Test
    void shouldUpdateReservationReservationBadRequestExceptionEmail() throws Exception {
        long reservationId = reservation1.getId();
        mockMvc.perform(put("/reservation/{reservationId}", reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"gameId\":1,\"name\":\"Juan\", \"lastName\":\"Perez\", \"documentNumber\":\"0001\", \"email\":\"mail1mail.com\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message", is("Invalid email on update request")));
    }

    @Test
    void shouldDeleteReservationById() throws Exception {
        long reservationId = reservation1.getId();
        given(reservationService.deleteReservationById(reservationId)).willReturn(true);
        mockMvc.perform(delete("/reservation/{reservationId}", reservationId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteReservationByIdReservationNotFoundException() throws Exception {
        long reservationId = reservation1.getId();
        given(reservationService.deleteReservationById(reservationId)).willReturn(false);
        mockMvc.perform(delete("/reservation/{reservationId}", reservationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Reservation " + reservationId + " does not exist")));
    }

    @Test
    void shouldDeleteReservationByGameId() throws Exception {
        long gameId = reservation1.getGameId();
        given(reservationService.deleteReservationByGameId(gameId)).willReturn(1L);
        mockMvc.perform(delete("/reservation/delete/{gameId}", gameId))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteReservationByGameIdReservationNotFoundException() throws Exception {
        long gameId = reservation1.getGameId();
        given(reservationService.deleteReservationByGameId(gameId)).willReturn(0L);
        mockMvc.perform(delete("/reservation/delete/{gameId}", gameId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("There is no reservations for game " + gameId)));
    }
}
