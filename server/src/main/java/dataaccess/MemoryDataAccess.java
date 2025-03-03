package dataaccess;

import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    final private HashMap<userName ,User> users = new HashMap<>();
    final private HashMap<authToken ,AuthToken> authTokens = new HashMap<>();
    final private HashMap<gameID ,Game> games = new HashMap<>();

    public User createUser(User user) {
        user = new User();

        return user;
    }
    public AuthToken createAuthToken(AuthToken authToken) {
        authToken = new AuthToken();

        return authToken;
    }

    public Collection<Game> listGames() { return games.values(); }

    public User getUser(String userName) { return users.get(userName); }
    public AuthToken getAuthToken(String authToken) { return authTokens.get(authToken); }

    public void deleteAuthToken(String authToken) { authTokens.remove(authToken); }
}
