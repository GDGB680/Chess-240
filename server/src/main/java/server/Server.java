package server;

import spark.Spark;
import dataaccess.*;
import service.*;
import com.google.gson.Gson;
import java.util.Map;

public class Server {
    private final DataAccess dataAccess;
    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public Server() {
        try {
            dataAccess = createDataAccess();
//            dataAccess = new MemoryDataAccess();
            userService = new UserService(dataAccess);
            gameService = new GameService(dataAccess);
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database access", e);
        }
    }

    private DataAccess createDataAccess() throws DataAccessException {
        return new MySQLDataAccess();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        configureClearEndpoint();
        configureRegisterEndpoint();
        configureLoginEndpoint();
        configureLogoutEndpoint();
        configureListGamesEndpoint();
        configureCreateGameEndpoint();
        configureJoinGameEndpoint();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void configureClearEndpoint() {
        Spark.delete("/db", (req, res) -> {
            try {
                dataAccess.clear();
                res.status(200);
                return gson.toJson(Map.of());
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });
    }

    private void configureRegisterEndpoint() {
        Spark.post("/user", (req, res) -> {
            try {
                validateRequestBody(req);
                RegisterRequest request = gson.fromJson(req.body(), RegisterRequest.class);
                validateRegistrationFields(request);

                RegisterResult result = userService.register(request);
                res.status(200);
                return gson.toJson(result);
            } catch (DataAccessException e) {
                return handleException(res, e);
            } catch (Exception e) {
                return handleGenericError(res, e);
            }
        });
    }

    private void configureLoginEndpoint() {
        Spark.post("/session", (req, res) -> {
            try {
                LoginRequest request = gson.fromJson(req.body(), LoginRequest.class);
                LoginResult result = userService.login(request);
                res.status(200);
                return gson.toJson(result);
            } catch (DataAccessException e) {
                return handleException(res, e);
            } catch (Exception e) {
                return handleGenericError(res, e);
            }
        });
    }

    private void configureLogoutEndpoint() {
        Spark.delete("/session", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                userService.logout(new LogoutRequest(authToken));
                res.status(200);
                return gson.toJson(Map.of());
            } catch (DataAccessException e) {
                return handleException(res, e);
            } catch (Exception e) {
                return handleGenericError(res, e);
            }
        });
    }

    private void configureListGamesEndpoint() {
        Spark.get("/game", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                ListGamesResult result = gameService.listGames(authToken);
                res.status(200);
                return gson.toJson(result);
            } catch (DataAccessException e) {
                return handleException(res, e);
            } catch (Exception e) {
                return handleGenericError(res, e);
            }
        });
    }

    private void configureCreateGameEndpoint() {
        Spark.post("/game", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                NewGameRequest request = gson.fromJson(req.body(), NewGameRequest.class);
                var result = gameService.createGame(request, authToken);
                res.status(200);
                return gson.toJson(result);
            } catch (DataAccessException e) {
                return handleException(res, e);
            } catch (Exception e) {
                return handleGenericError(res, e);
            }
        });
    }

    private void configureJoinGameEndpoint() {
        Spark.put("/game", (req, res) -> {
            try {
                String authToken = req.headers("Authorization");
                JoinGameRequest request = gson.fromJson(req.body(), JoinGameRequest.class);
                gameService.joinGame(request, authToken);
                res.status(200);
                return "{}";
            } catch (DataAccessException e) {
                return handleException(res, e);
            } catch (Exception e) {
                return handleGenericError(res, e);
            }
        });
    }

    private void validateRequestBody(spark.Request req) throws DataAccessException {
        if (req.body() == null || req.body().isEmpty()) {
            throw new DataAccessException("bad request");
        }
    }

    private void validateRegistrationFields(RegisterRequest request) throws DataAccessException {
        if (request == null || request.username() == null ||
                request.password() == null || request.email() == null) {
            throw new DataAccessException("bad request");
        }
    }

    private Object handleException(spark.Response res, DataAccessException e) {
        res.status(determineStatusCode(e.getMessage()));
        return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
    }

    private Object handleGenericError(spark.Response res, Exception e) {
        res.status(500);
        return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
    }

    private int determineStatusCode(String errorMessage) {
        return switch (errorMessage) {
            case "bad request" -> 400;
            case "unauthorized" -> 401;
            case "already taken" -> 403;
            default -> 500;
        };
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
