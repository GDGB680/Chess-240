package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySQLDataAccess implements DataAccess {
    private final Gson gson = new Gson();

    public MySQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    // Database Configuration
    private void configureDatabase() throws DataAccessException {
        String[] createStatements = {
                """
        CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(255) PRIMARY KEY,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL UNIQUE
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        """,
                """
        CREATE TABLE IF NOT EXISTS auth_tokens (
            auth_token VARCHAR(255) PRIMARY KEY,
            username VARCHAR(255) NOT NULL,
            FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
        )
        """,
                """
        CREATE TABLE IF NOT EXISTS games (
            game_id INT AUTO_INCREMENT PRIMARY KEY,
            white_username VARCHAR(255),
            black_username VARCHAR(255),
            game_name VARCHAR(255) NOT NULL,
            game_state TEXT NOT NULL,
            FOREIGN KEY (white_username) REFERENCES users(username) ON DELETE SET NULL,
            FOREIGN KEY (black_username) REFERENCES users(username) ON DELETE SET NULL
        )
        """
        };

        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: " + ex.getMessage());
        }
    }

    // User Operations
    @Override
    public UserData createUser(UserData user) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            if (getUser(user.getUsername()) != null) {
                throw new DataAccessException("already taken");
            }
            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();
            conn.commit();
            return new UserData(user.getUsername(), hashedPassword, user.getEmail());
        } catch (SQLException e) {
//            conn.rollback();
            if (e.getErrorCode() == 1062) { // MySQL duplicate entry code
                throw new DataAccessException("already taken");
            }
            throw new DataAccessException("Error creating user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting user: " + e.getMessage());
        }
    }

    // Auth Operations
    @Override
    public AuthData createAuthToken(AuthData authToken) throws DataAccessException {
        String sql = "INSERT INTO auth_tokens (auth_token, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken.getAuthToken());
            stmt.setString(2, authToken.getUsername());
            stmt.executeUpdate();
            return authToken;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating auth token: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM auth_tokens WHERE auth_token = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(
                            rs.getString("auth_token"),
                            rs.getString("username")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting auth token: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth_tokens WHERE auth_token = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        }
    }

    // Game Operations
    @Override
    public GameData createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO games (white_username, black_username, game_name, game_state) VALUES (?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, game.getWhiteUsername() == null ? null : game.getWhiteUsername());
            stmt.setString(2, game.getBlackUsername() == null ? null : game.getBlackUsername());
            stmt.setString(3, game.getGameName());
            stmt.setString(4, gson.toJson(game.getGame()));
            stmt.executeUpdate();

            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt(1),
                            game.getWhiteUsername(),
                            game.getBlackUsername(),
                            game.getGameName(),
                            game.getGame()
                    );
                }
                throw new DataAccessException("Failed to create game");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE game_id = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("game_id"),
                            rs.getString("white_username"),
                            rs.getString("black_username"),
                            rs.getString("game_name"),
                            gson.fromJson(rs.getString("game_state"), ChessGame.class)
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting game: " + e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM games";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                games.add(new GameData(
                        rs.getInt("game_id"),
                        rs.getString("white_username"),
                        rs.getString("black_username"),
                        rs.getString("game_name"),
                        gson.fromJson(rs.getString("game_state"), ChessGame.class)
                ));
            }
            return games;
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE games SET white_username = ?, black_username = ?, game_state = ? WHERE game_id = ?";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.getWhiteUsername());
            stmt.setString(2, game.getBlackUsername());
            stmt.setString(3, gson.toJson(game.getGame()));
            stmt.setInt(4, game.getGameID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    // Clear Operation
    @Override
    public void clear() throws DataAccessException {
        String[] tables = {"auth_tokens", "games", "users"};
        try (var conn = DatabaseManager.getConnection()) {
            for (var table : tables) {
                try (var stmt = conn.prepareStatement("TRUNCATE TABLE " + table)) {
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing database: " + e.getMessage());
        }
    }
}
