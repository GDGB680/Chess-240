package service;

import dataAccess.*;
import model.*;
import chess.ChessGame;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    @Test
    @DisplayName("Create Game Success")
    public void createGameSuccess() {
        // Setup
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        GameService gameService = new GameService(dataAccess);
        RegisterRequest registerRequest = new RegisterRequest("username1", "password1", "email1@example.com");

        try {
            RegisterResult registerResult = userService.register(registerRequest);
            NewGameRequest gameRequest = new NewGameRequest("Test Game");

            // Execute
            NewGameResult result = gameService.createGame(gameRequest, registerResult.authToken());

            // Verify
            assertNotNull(result);
            assertTrue(result.gameID() > 0);
        } catch (DataAccessException e) {
            fail("Should not have thrown an exception");
        }
    }

    @Test
    @DisplayName("Create Game Failure - Unauthorized")
    public void createGameFailureUnauthorized() {
        // Setup
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);
        NewGameRequest gameRequest = new NewGameRequest("Test Game");

        // Execute & Verify
        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> gameService.createGame(gameRequest, "invalid-token"));
        assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    @DisplayName("List Games Success")
    public void listGamesSuccess() {
        // Setup
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        GameService gameService = new GameService(dataAccess);
        RegisterRequest registerRequest = new RegisterRequest("username1", "password1", "email1@example.com");

        try {
            RegisterResult registerResult = userService.register(registerRequest);
            NewGameRequest gameRequest = new NewGameRequest("Test Game");
            gameService.createGame(gameRequest, registerResult.authToken());

            // Execute
            ListGamesResult result = gameService.listGames(registerResult.authToken());

            // Verify
            assertNotNull(result);
            assertNotNull(result.games());
            assertEquals(1, result.games().size());
        } catch (DataAccessException e) {
            fail("Should not have thrown an exception");
        }
    }

    @Test
    @DisplayName("List Games Failure - Unauthorized")
    public void listGamesFailureUnauthorized() {
        // Setup
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        // Execute & Verify
        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> gameService.listGames("invalid-token"));
        assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    @DisplayName("Join Game Success")
    public void joinGameSuccess() {
        // Setup
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        GameService gameService = new GameService(dataAccess);
        RegisterRequest registerRequest = new RegisterRequest("username1", "password1", "email1@example.com");

        try {
            RegisterResult registerResult = userService.register(registerRequest);
            NewGameRequest gameRequest = new NewGameRequest("Test Game");
            NewGameResult gameResult = gameService.createGame(gameRequest, registerResult.authToken());
            JoinGameRequest joinRequest = new JoinGameRequest("WHITE", gameResult.gameID());

            // Execute & Verify (no exception means success)
            assertDoesNotThrow(() -> gameService.joinGame(joinRequest, registerResult.authToken()));
        } catch (DataAccessException e) {
            fail("Should not have thrown an exception");
        }
    }

    @Test
    @DisplayName("Join Game Failure - Invalid Color")
    public void joinGameFailureInvalidColor() {
        // Setup
        DataAccess dataAccess = new MemoryDataAccess();
        UserService userService = new UserService(dataAccess);
        GameService gameService = new GameService(dataAccess);
        RegisterRequest registerRequest = new RegisterRequest("username1", "password1", "email1@example.com");

        try {
            RegisterResult registerResult = userService.register(registerRequest);
            NewGameRequest gameRequest = new NewGameRequest("Test Game");
            NewGameResult gameResult = gameService.createGame(gameRequest, registerResult.authToken());
            JoinGameRequest joinRequest = new JoinGameRequest("PURPLE", gameResult.gameID());

            // Execute & Verify
            DataAccessException exception = assertThrows(DataAccessException.class,
                    () -> gameService.joinGame(joinRequest, registerResult.authToken()));
            assertEquals("bad request", exception.getMessage());
        } catch (DataAccessException e) {
            fail("Setup should not have thrown an exception");
        }
    }
}
