package service;

import chess.*;
import dataAccess.*;
import model.*;
import java.util.Collection;
import java.util.Random;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public NewGameResult createGame(NewGameRequest request, String authToken) throws DataAccessException {
        if (request == null || request.gameName() == null || request.gameName().isEmpty()) {
            throw new DataAccessException("bad request");
        }
        AuthData authData = dataAccess.getAuthToken(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        int gameID = generateGameID();
        ChessGame newGame = new ChessGame();
        GameData newGameData = new GameData(gameID, null, null, request.gameName(), newGame);
        dataAccess.createGame(newGameData);
        return new NewGameResult(gameID);
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuthToken(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        Collection games = dataAccess.listGames();
        return new ListGamesResult(games);
    }

    public void joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        // Validate auth token
        AuthData authData = dataAccess.getAuthToken(authToken);
        if (authData == null) { throw new DataAccessException("unauthorized"); }
        // Validate game ID
        if (request.gameID() == null) { throw new DataAccessException("bad request"); }
        GameData gameData = dataAccess.getGame(request.gameID());
        if (gameData == null) { throw new DataAccessException("bad request"); }
        // Validate player color
        String playerColor = request.playerColor();
        // Invalid color
        if (playerColor == null || playerColor.isEmpty() ||
                (!playerColor.equals("WHITE") && !playerColor.equals("BLACK"))) {
            throw new DataAccessException("bad request");
        }
        // Check if spot is taken
        if (playerColor.equals("WHITE") && gameData.whiteUsername != null) {
            throw new DataAccessException("already taken");
        } else if (playerColor.equals("BLACK") && gameData.blackUsername != null) {
            throw new DataAccessException("already taken");
        }

        String username = authData.getUsername();
        GameData updatedGameData;
        if (request.playerColor().equals("WHITE")) {
            updatedGameData = new GameData(gameData.getGameID(), username,
                    gameData.getBlackUsername(), gameData.getGameName(),
                    gameData.getGame());
        } else {
            updatedGameData = new GameData(gameData.getGameID(),
                    gameData.getWhiteUsername(), username, gameData.getGameName(),
                    gameData.getGame());
        }
        dataAccess.updateGame(updatedGameData);
    }


    private int generateGameID() {
        Random random = new Random();
        return random.nextInt(1000);
    }
}
