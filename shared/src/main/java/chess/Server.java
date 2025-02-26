package chess;

import spark.Spark;


public class Server {
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

//        // Register your endpoints and handle exceptions here.
//        Spark.get("/hello", (req, res)->"Hello, world");
//
//        Spark.get("/hello/:name", (req, res)->{
//            return "Hello, "+ req.params(":name");
//        });


        //Clear application
        Spark.delete("/db", (req, res)->{
            return 0;
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
}