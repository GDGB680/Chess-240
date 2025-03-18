package server;

import spark.Spark;
import dataaccess.*;
import service.*;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

public class Server {
//    private final MemoryDataAccess dataAccess = new MemoryDataAccess();
    private final MySQLDataAccess dataAccess = new MySQLDataAccess();
    private final UserService userService = new UserService(dataAccess);
    private final GameService gameService = new GameService(dataAccess);


    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Gson gson = new Gson();

        //Clear application
        Spark.delete("/db", (req, res) -> {
            try {
                dataAccess.clear();
                res.status(200);
                return gson.toJson(new HashMap<>());
            } catch (Exception e) {
                res.status(500);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: " + e.getMessage());
                return gson.toJson(errorResponse);
            }
        });

        // Register
        Spark.post("/user", (req, res) -> {
            try {
                String body = req.body();
                if (body == null || body.isEmpty()) {
                    res.status(400);
                    return gson.toJson(Map.of("message", "Error: empty request body"));
                }
                RegisterRequest registerRequest = gson.fromJson(body, RegisterRequest.class);
                if (registerRequest == null || registerRequest.username()
                        == null || registerRequest.password() == null ||
                        registerRequest.email() == null) {
                    res.status(400);
                    return gson.toJson(Map.of("message", "Error: missing required fields"));
                }
                RegisterResult result = userService.register(registerRequest);
                res.status(200);
                return gson.toJson(result);
            } catch (DataAccessException e) {
                res.status(determineStatusCode(e.getMessage()));
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //Login
        Spark.post("/session", (req, res) -> {
            try {
                LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
                LoginResult loginResult = userService.login(loginRequest);
                res.status(200);
                return gson.toJson(loginResult);
            } catch (Exception e) {
                res.status(determineStatusCode(e.getMessage()));
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //Logout
        Spark.delete("/session", (req, res)->{
            try {
                String authToken = req.headers("authorization");
                LogoutRequest logoutRequest = new LogoutRequest(authToken);
                userService.logout(logoutRequest);
                res.status(200);
                return gson.toJson(new HashMap<>());
            } catch (DataAccessException e) {
                if (e.getMessage().equals("unauthorized")) {
                    res.status(401);
                } else {
                    res.status(500);
                }
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //List Games
        Spark.get("/game", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                ListGamesResult result = gameService.listGames(authToken);
                res.status(200);
                return gson.toJson(result);
            } catch (DataAccessException e) {
                res.status(determineStatusCode(e.getMessage()));
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //Create Game
        Spark.post("/game", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                NewGameRequest createRequest = gson.fromJson(req.body(), NewGameRequest.class);
                var result = gameService.createGame(createRequest, authToken);
                res.status(200);
                return gson.toJson(result);
            } catch (DataAccessException e) {
                res.status(determineStatusCode(e.getMessage()));
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //Join Game
        Spark.put("/game", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                JoinGameRequest joinRequest = gson.fromJson(req.body(), JoinGameRequest.class);
                gameService.joinGame(joinRequest, authToken);
                res.status(200);
                return "{}";
            } catch (DataAccessException e) {
                res.status(determineStatusCode(e.getMessage()));
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private int determineStatusCode(String errorMessage) {
        switch (errorMessage) {
            case "bad request":
                return 400;
            case "unauthorized":
                return 401;
            case "already taken":
                return 403;
            default:
                return 500;
        }
    }
}
