package server;
import spark.Spark;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;


public class Server {
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
                clearDatabase();
                res.status(200);
                return gson.toJson(new HashMap<>());
            } catch (Exception e) {
                res.status(500);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: " + e.getMessage());
                return gson.toJson(errorResponse);
            }
        });

        //Register
        Spark.post("/user", (req, res)->{
            try {
                UserRequest userRequest = gson.fromJson(req.body(), UserRequest.class);
                if (userRequest.username == null || userRequest.password == null || userRequest.email == null) {
                    res.status(400);
                    return gson.toJson(Map.of("message", "Error: bad request"));
                }
                // Check if the username is already taken
                if (users.containsKey(userRequest.username)) {
                    res.status(403);
                    return gson.toJson(Map.of("message", "Error: already taken"));
                }
                // Create new user and auth token
                User newUser = new User(userRequest.username, userRequest.password, userRequest.email);
                users.put(userRequest.username, newUser);
                String authToken = generateAuthToken();
                authTokens.put(authToken, userRequest.username);
                // Prepare and send the response
                res.status(200);
                return gson.toJson(Map.of("username", userRequest.username, "authToken", authToken));
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


    private void clearDatabase() {
        // Implement your database clearing logic here.
        // This is a placeholder.  Replace with your actual database clearing code.
        // For example, if you are using a HashMap to store data:
//         users.clear();
//         games.clear();
//         authTokens.clear();
        System.out.println("Database cleared"); // Replace this!  This is just a marker.
    }
}