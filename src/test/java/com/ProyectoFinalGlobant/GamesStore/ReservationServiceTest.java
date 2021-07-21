package com.ProyectoFinalGlobant.GamesStore;

import com.ProyectoFinalGlobant.GamesStore.exceptions.*;
import com.ProyectoFinalGlobant.GamesStore.models.GameModel;
import com.ProyectoFinalGlobant.GamesStore.models.ReservationModel;
import com.ProyectoFinalGlobant.GamesStore.repositories.ReservationRepository;
import com.ProyectoFinalGlobant.GamesStore.services.GameService;
import com.ProyectoFinalGlobant.GamesStore.services.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.ProyectoFinalGlobant.GamesStore.models.GameModel;
import com.ProyectoFinalGlobant.GamesStore.repositories.GameRepository;
import com.ProyectoFinalGlobant.GamesStore.services.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ReservationServiceTest {
    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository mockReservationRepository;

    @Mock
    private GameService mockGameService;

    private ReservationModel reservation1 = new ReservationModel(1L,1L,"Juan","Perez","jp@gmail.com","123456-7");
    private ReservationModel reservation2 = new ReservationModel(2L,2L,"Jorge","Perez","jp2@gmail.com","223456-7");
    private GameModel game1 = new GameModel(1L, "game1", "console1", "2021-07-15", 5L, "AVAILABLE");
    private ArrayList<ReservationModel> reservationList;

    @BeforeEach
    void fillList() {
        reservationService = new ReservationService();
        reservationList = new ArrayList<ReservationModel>();
        reservationList.add(reservation1);
        reservationList.add(reservation2);
    }

    @Test
    void ShouldGetReservation(){
        MockitoAnnotations.openMocks(this);
        when(mockReservationRepository.findAll()).thenReturn(reservationList);
        reservationService.getReservations();
        assertEquals(reservationList.size(),2);
    }
    @Test
    void ShouldGetReservationById(){
        Long idBefore = reservation1.getId();
        MockitoAnnotations.openMocks(this);
        when(mockReservationRepository.findById(reservation1.getId())).thenReturn(java.util.Optional.ofNullable(reservation1));
        reservationService.getReservationById(reservation1.getId());
        assertEquals(reservation1.getId(),idBefore);
    }
    @Test
    void ShouldGetReservationByGameId(){
        Long gameIdBefore = reservation1.getGameId();
        MockitoAnnotations.openMocks(this);
        when(mockReservationRepository.findByGameId(reservation1.getGameId())).thenReturn(reservationList);
        reservationService.getReservationByGameId(reservation1.getGameId());
        assertEquals(reservationList.size(),2);
    }
    @Test
    void ShouldSaveReservation() throws ReservationBadRequestException, GameBadStatusException {
        Long IdBefore = reservation1.getId();
        MockitoAnnotations.openMocks(this);
        when(mockReservationRepository.save(any())).thenReturn(reservation1);
        reservationService.saveReservation(reservation1);
        assertEquals(reservation1.getId(),IdBefore);
    }
    @Test
    void ShouldUpdateReservation() throws ReservationNotFoundException {
        Long IdBefore = reservation1.getId();
        MockitoAnnotations.openMocks(this);
        when(mockReservationRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(reservation1));
        when(mockReservationRepository.save(any())).thenReturn(reservation1);
        reservationService.updateReservation(reservation1, reservation1.getId());
        assertEquals(reservation1.getId(),IdBefore);
    }
    @Test
    void ShouldDeleteReservationById() throws ReservationNotFoundException, GameBadStatusException {
        Long IdBefore = reservation1.getId();
        MockitoAnnotations.openMocks(this);
        when(mockReservationRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(reservation1));
        when(mockGameService.updateGameQuantity(game1.getId(), 10)).thenReturn(game1);
        reservationService.deleteReservationById(reservation1.getId());
        assertEquals(reservation1.getId(),IdBefore);
    }
    @Test
    void ShouldDeleteReservationByGameId() throws ReservationNotFoundException, GameBadStatusException {
        Long gameIdBefore = reservation1.getGameId();
        MockitoAnnotations.openMocks(this);
        when(mockReservationRepository.findByGameId(any())).thenReturn(reservationList);
        when(mockGameService.updateGameQuantity(game1.getId(), 10)).thenReturn(game1);
        reservationService.deleteReservationByGameId(reservation1.getGameId());
        assertEquals(reservation1.getGameId(),gameIdBefore);
    }
    @Test
    void ShouldFixDocument() {
        String documentBefore = reservation1.getDocumentNumber();
        MockitoAnnotations.openMocks(this);
        String documentAfter = reservationService.fixDocumentNumber(documentBefore);
        assertNotEquals(documentBefore,documentAfter);
    }

    //Exceptions
    @Test
    void ShouldSaveReservationReservationBadRequestException(){
        MockitoAnnotations.openMocks(this);
        when(mockReservationRepository.findByDocumentNumberAndGameId(any(),any())).thenReturn(reservationList);
        Exception exception = assertThrows(ReservationBadRequestException.class, () -> reservationService.saveReservation(reservation1));
        assertEquals("Game already reserved by user", exception.getMessage());
    }

    @Test
    void shouldUpdateReservationReservationNotFoundException(){
        MockitoAnnotations.openMocks(this);
        when(mockReservationRepository.findById(any())).thenReturn(Optional.empty());
        Exception exception = assertThrows(ReservationNotFoundException.class, () -> reservationService.updateReservation(reservation1, reservation1.getId()));
        assertEquals("Reservation does not exist, sorry.", exception.getMessage());
    }

    @Test
    void shouldDeleteReservationByIdReservationNotFoundExceptios(){
        MockitoAnnotations.openMocks(this);
        when(mockReservationRepository.findById(any())).thenReturn(Optional.empty());
        Exception exception = assertThrows(ReservationNotFoundException.class, () -> reservationService.deleteReservationById(reservation1.getId()));
        assertEquals("Reservation does not exist", exception.getMessage());
    }

}
