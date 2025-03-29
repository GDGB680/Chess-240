package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MySQLDataAccessTests {

    private MySQLDataAccess dao;
    private final UserData testUser = new UserData("testuser", "password", "test@email.com");
    private final AuthData testAuth = new AuthData("authtoken", "testuser");
    private final ChessGame testGame = new ChessGame();

    @BeforeEach
    void setUp() throws Exception {
        dao = new MySQLDataAccess();
        dao.clear(); // Ensure database is clean before each test
    }

    // User DAO Tests
    @Test
    @Order(1)
    void createUserPositive() throws Exception {
        UserData created = dao.createUser(testUser);
        assertNotNull(created);
        assertEquals(testUser.getUsername(), created.getUsername());
        assertNotEquals(testUser.getPassword(), created.getPassword()); // Password should be hashed
        assertTrue(BCrypt.checkpw("password", created.getPassword()));
    }

    @Test
    @Order(2)
    void createUserNegative() {
        assertThrows(DataAccessException.class, () -> {
            dao.createUser(testUser);
            dao.createUser(testUser); // Attempt to create a duplicate user
        });
    }

    @Test
    @Order(3)
    void getUserPositive() throws Exception {
        dao.createUser(testUser);
        UserData found = dao.getUser(testUser.getUsername());
        assertNotNull(found);
        assertEquals(testUser.getUsername(), found.getUsername());
    }

    @Test
    @Order(4)
    void getUserNegative() throws Exception {
        UserData found = dao.getUser("nonexistent");
        assertNull(found); // Non-existent user should return null
    }

    // Auth Token DAO Tests
    @Test
    @Order(5)
    void createAuthTokenPositive() throws Exception {
        dao.createUser(testUser);
        AuthData created = dao.createAuthToken(testAuth);
        assertNotNull(created);
        assertEquals(testAuth.getAuthToken(), created.getAuthToken());
        assertEquals(testAuth.getUsername(), created.getUsername());
    }

    @Test
    @Order(6)
    void createAuthTokenNegative() {
        assertThrows(DataAccessException.class, () -> {
            dao.createAuthToken(testAuth); // Attempt to create a token for a non-existent user
        });
    }

    @Test
    @Order(7)
    void getAuthTokenPositive() throws Exception {
        dao.createUser(testUser);
        dao.createAuthToken(testAuth);
        AuthData found = dao.getAuthToken(testAuth.getAuthToken());
        assertNotNull(found);
        assertEquals(testAuth.getAuthToken(), found.getAuthToken());
        assertEquals(testAuth.getUsername(), found.getUsername());
    }

    @Test
    @Order(8)
    void getAuthTokenNegative() throws Exception {
        AuthData found = dao.getAuthToken("nonexistenttoken");
        assertNull(found); // Non-existent token should return null
    }

    @Test
    @Order(9)
    void deleteAuthTokenPositive() throws Exception {
        dao.createUser(testUser);
        dao.createAuthToken(testAuth);
        dao.deleteAuthToken(testAuth.getAuthToken());
        AuthData found = dao.getAuthToken(testAuth.getAuthToken());
        assertNull(found); // Token should be deleted
    }

    // Game DAO Tests
    @Test
    @Order(10)
    void createGamePositive() throws Exception {
        GameData game = new GameData(0, null, null, "testgame", testGame);
        GameData created = dao.createGame(null, null, "testgame", game.game);
        assertNotNull(created);
        assertTrue(created.getGameID() > 0); // Auto-generated ID should be greater than 0
        assertEquals("testgame", created.getGameName());
    }

    @Test
    @Order(11)
    void createGameNegative() {
        assertThrows(DataAccessException.class, () -> {
            dao.createGame(null, null, "testgame", null); // Attempt to create a null game object
        });
    }

    @Test
    @Order(12)
    void getGamePositive() throws Exception {
        GameData game = new GameData(0, null, null, "testgame", testGame);
        GameData created = dao.createGame(null, null, "testgame", game.game);
        GameData found = dao.getGame(created.getGameID());
        assertNotNull(found);
        assertEquals(created.getGameID(), found.getGameID());
        assertEquals("testgame", found.getGameName());
    }

    @Test
    @Order(13)
    void getGameNegative() throws Exception {
        GameData found = dao.getGame(9999); // Non-existent game ID
        assertNull(found); // Should return null for non-existent game ID
    }

    @Test
    @Order(14)
    void listGamesPositive() throws Exception {
        dao.createGame(null, null, null,
                new GameData(0, null, null, "game1", testGame).getGame());
        dao.createGame(null, null, null,
                new GameData(0, null, null, "game2", testGame).getGame());

        Collection<GameData> games = dao.listGames();

        assertNotNull(games);
        assertEquals(2, games.size()); // Should list all games in the database
    }

    @Test
    @Order(15)
    void updateGamePositive() throws Exception {
        GameData game = new GameData(0, "whiteplayer", "blackplayer", "testgame", testGame);

        GameData created = dao.createGame(null, null, null, game.game);

        GameData updated = new GameData(
                created.getGameID(),
                "newwhiteplayer",
                "newblackplayer",
                "updatedgame",
                testGame);

        dao.updateGame(updated);

        GameData retrieved = dao.getGame(created.getGameID());

        assertNotNull(retrieved);

        assertEquals("newwhiteplayer", retrieved.getWhiteUsername());

    }
}