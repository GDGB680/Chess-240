package dataAccess;

import model.*;

import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    final private HashMap<String ,UserData> users = new HashMap<>();
    final private HashMap<String ,AuthData> authTokens = new HashMap<>();
    final private HashMap<Integer,GameData> games = new HashMap<>();

    public UserData createUser(UserData user) {
        user = new UserData(user.username , user.password, user.email);
        users.put(user.username, user);
        return user;
    }
    public AuthData createAuthToken(AuthData authToken) {
        authToken = new AuthData(authToken.authToken, authToken.username);
        authTokens.put(authToken.authToken, authToken);  // STORE BY TOKEN
        return authToken;
    }
    public GameData createGame(GameData game) {
        game = new GameData(game.gameID, game.whiteUsername, game.blackUsername, game.gameName, game.game);
        games.put(game.gameID, game);
        return game;
    }

    public Collection<GameData> listGames() { return games.values(); }

    public UserData getUser(String username) { return users.get(username); }
    public AuthData getAuthToken(String token) { return authTokens.get(token); }
    public GameData getGame(int gameID) { return games.get(gameID); }

    public void updateGame(GameData game) {
        if (games.containsKey(game.gameID)) {
            games.put(game.gameID, game);
        } else { throw new RuntimeException("Game not found"); }
    }

    public void deleteAuthToken(String token) { authTokens.remove(token); }

    public void clear() {
        users.clear();
        authTokens.clear();
        games.clear();
    }
}
