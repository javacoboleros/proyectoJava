package com.ProyectoFinalGlobant.GamesStore.controllers;

import com.ProyectoFinalGlobant.GamesStore.models.ReservationModel;
import com.ProyectoFinalGlobant.GamesStore.services.ReservationService;
import com.ProyectoFinalGlobant.GamesStore.exceptions.ReservationBadRequestException;
import com.ProyectoFinalGlobant.GamesStore.exceptions.ReservationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    @GetMapping
    public ArrayList<ReservationModel> getReservations() {
        return reservationService.getReservations();
    }

    @GetMapping(path = "/{id}")
    public Optional<ReservationModel> getReservationsById(@PathVariable("id") Integer id) throws ReservationNotFoundException {
        Optional<ReservationModel> response = reservationService.getReservationById(id);
        if (response.isEmpty()) {
            throw new ReservationNotFoundException("Reservation " + id + " does not exist");
        }
        return response;
    }

    @GetMapping(path = "/game/{gameId}")
    public ArrayList<ReservationModel> getReservationsByGameId(@PathVariable("gameId") Integer gameId) throws ReservationNotFoundException {
        ArrayList<ReservationModel> response = reservationService.getReservationByGameId(gameId);
        if (response.isEmpty()) {
            throw new ReservationNotFoundException("There is no reservations for game " + gameId);
        }
        return response;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ReservationModel createReservation(@RequestBody ReservationModel reservation) throws ReservationBadRequestException {
        if ((reservation.getGameId() == 0) ||
                (reservation.getDocumentNumber() == null) ||
                (reservation.getName() == null) ||
                (reservation.getLastName() == null) ||
                (reservation.getEmail() == null)){
            throw new ReservationBadRequestException("Empty field on creation request");
        }
        if (!emailIsValid(reservation.getEmail())) throw new ReservationBadRequestException("Invalid email on creation request");
        try {
            return reservationService.saveReservation(reservation);
        } catch (DataIntegrityViolationException e) {
            throw new ReservationBadRequestException("User " + reservation.getDocumentNumber() + " has already reserved game " + reservation.getGameId());
        }
    }

    @PostMapping(path ="{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ReservationModel updateReservation(@RequestBody ReservationModel reservation, @PathVariable("id") Integer id)
            throws ReservationBadRequestException {
        if ((reservation.getGameId() == 0) ||
                (reservation.getDocumentNumber() == null) ||
                (reservation.getName() == null) ||
                (reservation.getLastName() == null) ||
                (reservation.getEmail() == null)){
            throw new ReservationBadRequestException("Empty field on creation request");
        }
        if (!emailIsValid(reservation.getEmail())) throw new ReservationBadRequestException("Invalid email on creation request");
        try {
            return reservationService.updateReservation(reservation, id);
        } catch (DataIntegrityViolationException e) {
            throw new ReservationBadRequestException("User " + reservation.getDocumentNumber() + " has already reserved game " + reservation.getGameId());
        }
    }

    @DeleteMapping(path = "/{id}")
    public String deleteById(@PathVariable("id") Integer id) throws ReservationNotFoundException {
        boolean ok = this.reservationService.deleteReservationById(id);
        if (ok) {
            return "Reservation " + id + " has been eliminated";
        } else {
            throw new ReservationNotFoundException("Reservation " + id + " does not exist");
        }
    }

    @DeleteMapping(path = "/delete/{gameId}")
    public String deleteByGameId(@PathVariable("gameId") Integer gameId) throws ReservationNotFoundException {
        long eliminations = this.reservationService.deleteReservationByGameId(gameId);
        if (eliminations > 0) {
            return eliminations + " reservations for game " + gameId + " have been eliminated";
        } else {
            throw new ReservationNotFoundException("There is no reservations for game " + gameId);
        }
    }

    public boolean emailIsValid(String email) {
        return email.contains("@") &&
                (email.endsWith(".com") || email.endsWith(".cl"));
    }
}