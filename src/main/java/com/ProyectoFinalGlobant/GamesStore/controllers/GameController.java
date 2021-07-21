package com.ProyectoFinalGlobant.GamesStore.controllers;

import com.ProyectoFinalGlobant.GamesStore.exceptions.GameAlreadyExistException;
import com.ProyectoFinalGlobant.GamesStore.exceptions.GameBadRequestException;
import com.ProyectoFinalGlobant.GamesStore.exceptions.GameBadStatusException;
import com.ProyectoFinalGlobant.GamesStore.exceptions.GameNotExistException;
import com.ProyectoFinalGlobant.GamesStore.models.GameModel;
import com.ProyectoFinalGlobant.GamesStore.services.GameService;
import com.ProyectoFinalGlobant.GamesStore.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

   @Autowired
    private GameService gameService;

   @Autowired
   private ReservationService reservationService;
   //CREATE GAME
   @PostMapping("/create")
   public ResponseEntity<Void> createGames(@RequestBody GameModel  game) throws GameAlreadyExistException, GameBadRequestException {
         gameService.createGame(game);
         return ResponseEntity.status(HttpStatus.CREATED).build();
   }

    //UPDATE GAME BY ID
    @PutMapping("/update/{id}")
    public String updateGame(@RequestBody GameModel game, @PathVariable("id") Long id) throws GameBadRequestException,GameAlreadyExistException {

           gameService.updateGame(game, id);

           return "INFO: Game Id: "+ id +" updated correctly";
    }

    //DELETE GAME BY ID
    @DeleteMapping("/delete/{id}")
    public String deleteGameById(@PathVariable("id") Long id) throws GameNotExistException, GameBadStatusException {
        reservationService.deleteReservationByGameId(id);
        gameService.deleteGameById(id);
       return  ("INFO: Game was removed successfully!!");

    }

    //GET ALL GAMES
    @GetMapping("/getAll")
    public List<GameModel> getAllGames(){
       return gameService.getAllGames();
   }


   //GET GAME BY STATUS
    @GetMapping("/getStatus/{status}")
    public List<GameModel> getGameByStatus(@PathVariable("status") String status) throws GameNotExistException {
      return gameService.getByStatus(status);
    }

    //GET GAME BY ID
    @GetMapping("/getById/{id}")
    public GameModel getGameById(@PathVariable("id") Long id) throws GameNotExistException {
         return   gameService.getGameById(id);
    }


}
