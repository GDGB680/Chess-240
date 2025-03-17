package dataaccess;

import model.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySQLDataAccess implements DataAccess {

    public void MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public UserData createUser(UserData user) throws DataAccessException {
        return null;
    }

    public UserData getUser(String userName) throws DataAccessException {
        return null;
    }

    public GameData createGame(GameData game) throws DataAccessException {
        return null;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    public void updateGame(GameData game) throws DataAccessException {

    }

    public AuthData createAuthToken(AuthData authToken) throws DataAccessException {
        return null;
    }

    public AuthData getAuthToken(String authToken) throws DataAccessException {
        return null;
    }

    public void deleteAuthToken(String authToken) throws DataAccessException {

    }

    public void clear() throws DataAccessException {

    }


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT 1+1")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
