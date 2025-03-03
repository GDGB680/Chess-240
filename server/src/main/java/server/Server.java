package server;
import spark.Spark;
import dataaccess.*;
import model.*;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;


public class Server {
    private final MemoryDataAccess dataAccess = new MemoryDataAccess();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Gson gson = new Gson();
//        // Register your endpoints and handle exceptions here.
//        Spark.get("/hello", (req, res)->"Hello, world");
//
//        Spark.get("/hello/:name", (req, res)->{
//            return "Hello, "+ req.params(":name");
//        });


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
                UserData userRequest = gson.fromJson(req.body(), UserData.class);
                if (userRequest.username == null || userRequest.password == null || userRequest.email == null) {
                    res.status(400);
                    return gson.toJson(Map.of("message", "Error: bad request"));
                }

                // if already taken
                if (dataAccess.getUser(userRequest.username) != null) {
                    res.status(403);
                    return gson.toJson(Map.of("message", "Error: already taken"));
                }

                // new user and auth token
                UserData newUser = dataAccess.createUser(userRequest);
                String authToken = generateAuthToken();
                AuthData newAuthData = dataAccess.createAuthToken(new AuthData(authToken, newUser.username));

                // response
                res.status(200);
                return gson.toJson(Map.of("username", newUser.username, "authToken", newAuthData.authToken));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        //Login
        Spark.post("/session", (req, res)->{
            return 0;
        });
        //Logout
        Spark.delete("/session", (req, res)->{
            return 0;
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