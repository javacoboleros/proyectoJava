package com.ProyectoFinalGlobant.GamesStore.services;

import com.ProyectoFinalGlobant.GamesStore.exceptions.GameAlreadyExistException;
import com.ProyectoFinalGlobant.GamesStore.exceptions.GameBadRequestException;
import com.ProyectoFinalGlobant.GamesStore.exceptions.GameBadStatusException;
import com.ProyectoFinalGlobant.GamesStore.exceptions.GameNotExistException;
import com.ProyectoFinalGlobant.GamesStore.models.GameModel;
import com.ProyectoFinalGlobant.GamesStore.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    //POST CREATE GAMES
    public void createGame(@RequestBody GameModel game) throws GameAlreadyExistException, GameBadRequestException {

        GameModel  gameModel= gameRepository.findByTitleAndConsole(game.getTitle().toUpperCase(),game.getConsole().toUpperCase());

        if(gameModel != null ){
            throw new GameAlreadyExistException ("ALERT: Game already exist in database", game.getTitle());
        }
        if (game.getCreationDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
            game.setTitle(game.getTitle().toUpperCase());
            game.setConsole(game.getConsole().toUpperCase());
            game.setStatus("AVAILABLE");
            Long gameCopies = game.getCopies();
            if(gameCopies <= 0){
                throw new GameBadRequestException("ALERT. Can't create game without copies :/");
            }
            gameRepository.save(game);
        }else {
            throw new GameBadRequestException("ALERT. Wrong format date, Use yyyy-MM-dd format.");
        }
    }

    //UPDATE GAME
    public void updateGame(GameModel game, @PathVariable("id") Long id) throws GameBadRequestException,GameAlreadyExistException {

        GameModel  gameModel= gameRepository.findByTitleAndConsole(game.getTitle().toUpperCase(),game.getConsole().toUpperCase());

        if(gameModel != null && gameModel.getId() != id){
            throw new GameAlreadyExistException ("ALERT: Game already exist in database", game.getTitle());
        }
        if (game.getCreationDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
            Long number = game.getCopies();
            if (number == 0) {
                game.setStatus("RESERVED");
            }
            if (number > 0) {
                game.setStatus("AVAILABLE");
            }
            if (number < 0) {
                throw new GameBadRequestException("The game can't have negative copies.");
            }
            game.setId(id);
            gameRepository.save(game);
        }else {
            throw new GameBadRequestException("ALERT. Wrong format date, Use yyyy-MM-dd format.");
        }
    }

    public GameModel updateGameQuantity(Long id, Integer quantity) throws GameBadStatusException {
        Optional<GameModel> game = gameRepository.findById(id);
            if (game.isEmpty()){
                throw new GameNotExistException("The game does not exist, sorry");
            }else {
                game.get().setCopies(game.get().getCopies()+quantity);

                if(game.get().getCopies() > 0 ){
                    game.get().setStatus("AVAILABLE");
                }
                if (game.get().getCopies() == 0){
                    game.get().setStatus("RESERVED");
                }
                if (game.get().getCopies() < 0){
                    throw new GameBadStatusException("There are no available copies for this game.", game.get().getTitle());
                }else {
                    return gameRepository.save(game.get());
                }
        }
    }

    //DELETE BY ID
    public void deleteGameById(@PathVariable("id") Long id) throws GameNotExistException {
        Optional<GameModel> gameOptional = gameRepository.findById(id);

        if(gameOptional.isEmpty()){
            throw new GameNotExistException("ALERT: Game not exist in database!");
        }

        gameRepository.deleteById(id);

    }

    //GET ALL GAMES
    public List<GameModel> getAllGames() {

        return gameRepository.findAll(Sort.by("title").ascending());
    }

    //GET GAME BY STATUS
    public List<GameModel> getByStatus(@PathVariable("status") String status) throws GameNotExistException{

        List<GameModel> gameStatus = gameRepository.findByStatus(status);

        if (gameStatus.isEmpty()){
            throw new GameNotExistException("ALERT: Games with status:" +status+ " not exist in Database!");
        }

        return gameRepository.findByStatus(status);

    }

    //GET GAME BY ID
    public GameModel getGameById(@PathVariable("id") Long id) throws GameNotExistException  {
        Optional<GameModel> gameOptional = gameRepository.findById(id);

        if(gameOptional.isEmpty()){
            throw new GameNotExistException("ALERT: Game Id:" +id+ " not exist in Database!");
        }

       return gameOptional.get();
    }



}
