//package dataaccess;
//
//import chess.ChessGame;
//import model.*;
//import org.junit.jupiter.api.*;
//import org.mindrot.jbcrypt.BCrypt;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class MySQLDataAccessTests {
//    private MySQLDataAccess dao;
//    private final UserData testUser = new UserData("testuser", "password", "test@email.com");
//    private final AuthData testAuth = new AuthData("authtoken", "testuser");
//    private final ChessGame testGame = new ChessGame();
//
//    @BeforeEach
//    void setUp() throws Exception {
//        dao = new MySQLDataAccess();
//        dao.clear();
//    }
//
//    // User DAO Tests
//    @Test
//    void createUserPositive() throws Exception {
//        UserData created = dao.createUser(testUser);
//        assertNotNull(created);
//        assertEquals(testUser.getUsername(), created.getUsername());
//    }
//
//    @Test
//    void createUserNegative() {
//        assertThrows(DataAccessException.class, () -> {
//            dao.createUser(testUser);
//            dao.createUser(testUser); // Duplicate user
//        });
//    }
//
//    @Test
//    void getUserPositive() throws Exception {
//        dao.createUser(testUser);
//        UserData found = dao.getUser(testUser.getUsername());
//        assertEquals(testUser.getUsername(), found.getUsername());
//    }
//
//    @Test
//    void getUserNegative() throws Exception {
//        UserData found = dao.getUser("nonexistent");
//        assertNull(found);
//    }
//
//    // Auth DAO Tests
//    @Test
//    void createAuthTokenPositive() throws Exception {
//        dao.createUser(testUser);
//        AuthData created = dao.createAuthToken(testAuth);
//        assertEquals(testAuth.getAuthToken(), created.getAuthToken());
//    }
//
//    @Test
//    void createAuthTokenNegative() {
//        assertThrows(DataAccessException.class, () -> {
//            dao.createAuthToken(testAuth); // No user exists
//        });
//    }
//
//    @Test
//    void getAuthTokenPositive() throws Exception {
//        dao.createUser(testUser);
//        dao.createAuthToken(testAuth);
//        AuthData found = dao.getAuthToken(testAuth.getAuthToken());
//        assertNotNull(found);
//    }
//
//    @Test
//    void getAuthTokenNegative() throws Exception {
//        AuthData found = dao.getAuthToken("badtoken");
//        assertNull(found);
//    }
//
//    @Test
//    void deleteAuthTokenPositive() throws Exception {
//        dao.createUser(testUser);
//        dao.createAuthToken(testAuth);
//        dao.deleteAuthToken(testAuth.getAuthToken());
//        assertNull(dao.getAuthToken(testAuth.getAuthToken()));
//    }
//
//    // Game DAO Tests
//    @Test
//    void createGamePositive() throws Exception {
//        GameData game = new GameData(0, null, null, "testgame", testGame);
//        GameData created = dao.createGame(null, null, "testgame", testGame);
//        assertTrue(created.getGameID() > 0);
//    }
//
//    @Test
//    void createGameNegative() {
//        assertThrows(DataAccessException.class, () -> {
//            dao.createGame(null, null, null, null); // Invalid input
//        });
//    }
//
//    @Test
//    void getGamePositive() throws Exception {
//        GameData created = dao.createGame(null, null, "test", testGame);
//        GameData found = dao.getGame(created.getGameID());
//        assertEquals(created.getGameID(), found.getGameID());
//    }
//
//    @Test
//    void getGameNegative() throws Exception {
//        assertNull(dao.getGame(9999)); // Non-existent ID
//    }
//
//    @Test
//    void listGamesPositive() throws Exception {
//        dao.createGame(new GameData(0, null, null, "game1", testGame));
//        dao.createGame(new GameData(0, null, null, "game2", testGame));
//        assertEquals(2, dao.listGames().size());
//    }
//
//    @Test
//    void updateGamePositive() throws Exception {
//        GameData game = dao.createGame(new GameData(0, null, null, "test", testGame));
//        GameData updated = new GameData(game.getGameID(), "white", "black",
//                "updated", testGame);
//        dao.updateGame(updated);
//
//        GameData retrieved = dao.getGame(game.getGameID());
//        assertEquals("white", retrieved.getWhiteUsername());
//        assertEquals("black", retrieved.getBlackUsername());
//    }
//
//    @Test
//    void updateGameNegative() {
//        assertThrows(DataAccessException.class, () -> {
//            dao.updateGame(new GameData(9999, null, null, "nonexistent", testGame));
//        });
//    }
//
//    @Test
//    void clearPositive() throws Exception {
//        dao.createUser(testUser);
//        dao.createAuthToken(testAuth);
//        dao.createGame(new GameData(0, null, null, "test", testGame));
//
//        dao.clear();
//
//        assertEquals(0, dao.listGames().size());
//        assertNull(dao.getUser(testUser.getUsername()));
//        assertNull(dao.getAuthToken(testAuth.getAuthToken()));
//    }
//
//    // Security Tests
//    @Test
//    void passwordHashingTest() throws Exception {
//        String rawPassword = "secret";
//        UserData user = new UserData("hashuser", rawPassword, "hash@test.com");
//        UserData created = dao.createUser(user);
//
//        assertNotEquals(rawPassword, created.getPassword());
//        assertTrue(BCrypt.checkpw(rawPassword, created.getPassword()));
//    }
//
//    // Game State Persistence Test
//    @Test
//    void gameStateSerializationTest() throws Exception {
//        ChessGame originalGame = new ChessGame();
//        originalGame.setTeamTurn(ChessGame.TeamColor.WHITE);
//
//        GameData game = dao.createGame(new GameData(0, null, null, "serialize", originalGame));
//        GameData retrieved = dao.getGame(game.getGameID());
//
//        assertEquals(originalGame.getTeamTurn(), retrieved.getGame().getTeamTurn());
//    }
//}
