package dataaccess;


public interface DataAccess {

    User createUser(User user) throws DataAccessException;
    User getUser(String userName) throws DataAccessException;

    Game createGame(Game game) throws DataAccessException;
    Game getGame(int gameID) throws DataAccessException;
    Collection<Game> listGames() throws DataAccessException;
    updateGame

    AuthToken createAuthToken(AuthToken authToken) throws DataAccessException;
    AuthToken getAuthToken(String authToken) throws DataAccessException;
    void deleteAuthToken(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;
}
