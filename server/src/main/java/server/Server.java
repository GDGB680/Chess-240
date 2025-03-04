package server;
import spark.Spark;
import dataaccess.*;
import model.*;
import service.*;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;


public class Server {
    private final MemoryDataAccess dataAccess = new MemoryDataAccess();
    private final UserService userService = new UserService(dataAccess);

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
                RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
                RegisterResult result = userService.register(registerRequest);
                res.status(200);
                return gson.toJson(result);
            } catch (DataAccessException e) {
                if (e.getMessage().equals("bad request")) {
                    res.status(400);
                } else if (e.getMessage().equals("already taken")) {
                    res.status(403);
                } else { res.status(500); }
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });
        //Login
        Spark.post("/session", (req, res)->{
            try {
                LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
                LoginResult loginResult = userService.login(loginRequest);

                res.status(200);
                return gson.toJson(loginResult);

            } catch (Exception e) {
                res.status(500);
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
                if (e.getMessage().equals("unauthorized")) { res.status(401);
                } else { res.status(500); }
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //List Games
        Spark.get("/game", (req, res)->{
            return 0;
        });
        //Create Game
        Spark.post("/game", (req, res)->{
            return 0;
        });
        //Join Game
        Spark.put("/game", (req, res)->{
            return 0;
        });



        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


    private String generateAuthToken() {
        return java.util.UUID.randomUUID().toString();
    }

    private void clearDatabase() {
        dataAccess.clear();
    }


}