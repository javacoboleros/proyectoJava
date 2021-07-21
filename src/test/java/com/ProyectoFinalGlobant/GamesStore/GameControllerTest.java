package com.ProyectoFinalGlobant.GamesStore;

import com.ProyectoFinalGlobant.GamesStore.controllers.GameController;
import com.ProyectoFinalGlobant.GamesStore.models.GameModel;
import com.ProyectoFinalGlobant.GamesStore.services.GameService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    MockMvc mockMvc2;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private GameService mockGameService;

    private GameModel game1 = new GameModel(1L, "game1", "console1", "2021-07-15", 5L, "AVAILABLE");
    private GameModel game2 = new GameModel(1L, "game1", "console1", "2021-07-15", 5L, "AVAILABLE");
    private ArrayList<GameModel> gameList;

    @BeforeEach
    void fillList() {
        gameList = new ArrayList<GameModel>();
        gameList.add(game1);
        gameList.add(game2);
    }

    @Test
    void shouldGetAllGames() throws Exception {
        given(mockGameService.getAllGames()).willReturn(gameList);
        mockMvc2.perform(get("/games/getAll"))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldGetOneGameByID() throws Exception {
        long gameId = game1.getId();
        given(mockGameService.getGameById(gameId)).willReturn(game1);
        mockMvc2.perform(get("/games/getById/{gameId}", gameId))
                .andExpect(jsonPath("$.title", is(game1.getTitle())));
    }

    @Test
    void shouldGetAllGamesByStatus() throws Exception {
        String gameStatus = game1.getStatus();
        given(mockGameService.getByStatus(gameStatus)).willReturn(gameList);
        mockMvc2.perform(get("/games/getStatus/{gameStatus}", gameStatus))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldCreateNewGame() throws Exception {
        doNothing().when(mockGameService).createGame(any());
        mockMvc2.perform(post("/games/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\":\"game1\", \"console\":\"console1\", \"creationDate\":\"2021-07-15\", \"copies\":5 }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateGame() throws Exception {
        long id = game1.getId();
        doNothing().when(mockGameService).updateGame(any(), any());
        mockMvc2.perform(put("/games/update/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\":\"game1\", \"console\":\"console1\", \"creationDate\":\"2021-07-15\", \"copies\":5 }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteGameById() throws Exception {
        long gameId = game1.getId();
        doNothing().when(mockGameService).deleteGameById(gameId);
        mockMvc2.perform(delete("/games/delete/{gameId}", gameId))
                .andExpect(status().isOk());
    }
}
