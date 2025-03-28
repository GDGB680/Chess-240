package model;

import chess.ChessGame;
import java.util.Objects;

public class GameData {
    public final int gameID;
    public final String whiteUsername;
    public final String blackUsername;
    public final String gameName;
    public final ChessGame game;

    public GameData(int gameID, String whiteUsername, String blackUsername,
                    String gameName, ChessGame game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public int getGameID() {return gameID;}
    public String getWhiteUsername() {return whiteUsername;}
    public String getBlackUsername() {return blackUsername;}
    public String getGameName() {return gameName;}
    public ChessGame getGame() {return game;}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameData gameData = (GameData) o;
        return gameID == gameData.gameID && Objects.equals(whiteUsername, gameData.whiteUsername)
                && Objects.equals(blackUsername, gameData.blackUsername)
                && Objects.equals(gameName, gameData.gameName)
                && Objects.equals(game, gameData.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public String toString() {
        return "GameData{" +
                "gameID=" + gameID +
                ", whiteUsername='" + whiteUsername + '\'' +
                ", blackUsername='" + blackUsername + '\'' +
                ", gameName='" + gameName + '\'' +
                ", game=" + game +
                '}';
    }
}
