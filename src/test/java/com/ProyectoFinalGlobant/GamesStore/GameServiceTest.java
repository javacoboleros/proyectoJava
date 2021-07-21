package com.ProyectoFinalGlobant.GamesStore;
import com.ProyectoFinalGlobant.GamesStore.exceptions.GameAlreadyExistException;
import com.ProyectoFinalGlobant.GamesStore.exceptions.GameBadRequestException;
import com.ProyectoFinalGlobant.GamesStore.exceptions.GameBadStatusException;
import com.ProyectoFinalGlobant.GamesStore.exceptions.GameNotExistException;
import com.ProyectoFinalGlobant.GamesStore.models.GameModel;
import com.ProyectoFinalGlobant.GamesStore.repositories.GameRepository;
import com.ProyectoFinalGlobant.GamesStore.services.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class GameServiceTest {
	@InjectMocks
	private GameService gameService;
	@Mock
	private GameRepository gameRepository;
	private GameModel game1 = new GameModel(1L, "game1", "console1", "2021-07-15", 5L, "AVAILABLE");
	private GameModel game2 = new GameModel(1L, "game1", "console1", "2021-07-15", 5L, "AVAILABLE");
	private ArrayList<GameModel> gameList;
	@BeforeEach
	void fillList() {
		gameService = new GameService();
		gameList = new ArrayList<GameModel>();
		gameList.add(game1);
		gameList.add(game2);
	}
	@Test
	void shouldCreateGame() throws GameBadRequestException {
		Long idBefore = game1.getId();
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findByTitleAndConsole(any(), any())).thenReturn(null);
		when(gameRepository.save(any())).thenReturn(game1);
		gameService.createGame(game1);
		assertEquals(idBefore, game1.getId());
	}
	@Test
	void shouldUpdateGame() throws GameBadRequestException {
		Long idBefore = game1.getId();
		Long number = game1.getCopies();
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(game1));
		when(gameRepository.save(any())).thenReturn(game1);
		gameService.updateGame(game1, game1.getId());
		assertEquals(idBefore, game1.getId());
	}
	@Test
	void shouldUpdateGameQuantity() throws GameBadStatusException {
		Long idBefore = game1.getId();
		Long copies = 10L;
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(game1));
		game1.setCopies(copies);
		when(gameRepository.save(any())).thenReturn(game1);
		gameService.updateGameQuantity(game1.getId(), 1);
		copies++;
		assertEquals(copies, game1.getCopies());
	}
	@Test
	void shouldDeleteGameById() throws GameBadRequestException {
		Long idBefore = game1.getId();
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(game1));
		doNothing().when(gameRepository).deleteById(game1.getId());
		gameService.deleteGameById(game1.getId());
		assertEquals(idBefore,game1.getId());
	}
	@Test
	void shouldGetAllGames() {
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findAll(Sort.by("title").ascending())).thenReturn(gameList);
		List<GameModel> response = gameService.getAllGames();
		assertEquals(gameList.size(), response.size());
	}

	@Test
	void shouldGetByStatus() throws GameNotExistException {
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findByStatus("AVAILABLE")).thenReturn(gameList);
		List<GameModel> response = gameService.getByStatus(game1.getStatus());
		assertEquals(response.size(),2);
	}

	@Test
	void shouldGetGameById() throws GameNotExistException {
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findById(game1.getId())).thenReturn(java.util.Optional.ofNullable(game1));
		GameModel response = gameService.getGameById(game1.getId());
		assertEquals(response.getId(), game1.getId());
	}

	@Test
	void shouldGetGameByIdGameNotExistException() {
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findById(game1.getId())).thenReturn(Optional.empty());
		Exception exception =
				assertThrows(GameNotExistException.class, () -> gameService.getGameById(game1.getId()));
		assertEquals("ALERT: Game Id:" +game1.getId()+ " not exist in Database!", exception.getMessage());
	}

	@Test
	void shouldGetGameByStatusGameNotExistException() {
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findByStatus("AVAILABLE")).thenReturn(new ArrayList<GameModel>());
		Exception exception =
				assertThrows(GameNotExistException.class, () -> gameService.getByStatus(game1.getStatus()));
		assertEquals("ALERT: Games with status:" +game1.getStatus()+ " not exist in Database!", exception.getMessage());
	}

	@Test
	void shouldDeleteByIdGameNotExistException() {
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findById(game1.getId())).thenReturn(Optional.empty());
		Exception exception =
				assertThrows(GameNotExistException.class, () -> gameService.deleteGameById(game1.getId()));
		assertEquals("ALERT: Game not exist in database!", exception.getMessage());
	}

	@Test
	void shouldUpdateGameQuantityGameNotExistException() {
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findById(game1.getId())).thenReturn(Optional.empty());
		Exception exception =
				assertThrows(GameNotExistException.class, () -> gameService.updateGameQuantity(game1.getId(), 1));
		assertEquals("The game does not exist, sorry", exception.getMessage());
	}

	@Test
	void shouldUpdateGameQuantityGameBadStatusException() {
		MockitoAnnotations.openMocks(this);
		game1.setCopies(0L);
		when(gameRepository.findById(game1.getId())).thenReturn(Optional.ofNullable(game1));
		Exception exception =
				assertThrows(GameBadStatusException.class, () -> gameService.updateGameQuantity(game1.getId(), -1));
		assertEquals("There are no available copies for this game.", exception.getMessage());
	}

	@Test
	void shouldUpdateGameGameBadRequestException() {
		MockitoAnnotations.openMocks(this);
		game1.setCopies(-1L);
		Exception exception =
				assertThrows(GameBadRequestException.class, () -> gameService.updateGame(game1, game1.getId()));
		assertEquals("The game can't have negative copies.", exception.getMessage());
	}

	@Test
	void shouldUpdateGameGameAlreadyExistException() {
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findByTitleAndConsole(any(),any())).thenReturn(game1);
		Exception exception =
				assertThrows(GameAlreadyExistException.class, () -> gameService.updateGame(game1, game1.getId()));
		assertEquals("ALERT: Game already exist in database", exception.getMessage());


	}

	@Test
	void shouldCreateGameGameAlreadyExistException() {
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findByTitleAndConsole(any(),any())).thenReturn(game1);
		Exception exception =
				assertThrows(GameAlreadyExistException.class, () -> gameService.createGame(game1));
		assertEquals("ALERT: Game already exist in database", exception.getMessage());
	}

	@Test
	void shouldCreateGameGameBadRequestException() {
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findByTitleAndConsole(any(),any())).thenReturn(null);
		game1.setCopies(0L);
		Exception exception =
				assertThrows(GameBadRequestException.class, () -> gameService.createGame(game1));
		assertEquals("ALERT. Can't create game without copies :/", exception.getMessage());
	}

	@Test
	void shouldCreateGameGameBadRequestExceptionDate() {
		MockitoAnnotations.openMocks(this);
		when(gameRepository.findByTitleAndConsole(any(),any())).thenReturn(null);
		game1.setCreationDate("10-10-2010");
		Exception exception =
				assertThrows(GameBadRequestException.class, () -> gameService.createGame(game1));
		assertEquals("ALERT. Wrong format date, Use yyyy-MM-dd format.", exception.getMessage());
	}
}
