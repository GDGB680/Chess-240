package chess;

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
                clearDatabase(); // Call a method to clear the database
                res.status(200); // Success status code
                return gson.toJson(new HashMap<>()); // Empty JSON object as specified
            } catch (Exception e) {
                res.status(500); // Internal Server Error
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Error: " + e.getMessage()); // Include error description
                return gson.toJson(errorResponse); // JSON error response
            }
        });

        //Register
        Spark.post("/user", (req, res)->{
            return 0;
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