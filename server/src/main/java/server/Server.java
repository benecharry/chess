package server;

import handler.ServerHandler;
import manager.ServiceManager;
import spark.*;

public class Server {
    //Server looks to the url path and forward it to the exact handler.
    //Request type!
    //Needs to parse the info and put it in an object call Request and sends it to the handler
    // process it and construct the response object that is translated to an HTTP object.
    private final ServerHandler serverHandler;

    public Server() {
        ServiceManager serviceManager = new ServiceManager();
        this.serverHandler = new ServerHandler(serviceManager);
    }

    public Server(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public int run(int desiredPort) {
        System.out.println("Initializing port.");
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        //TO-DO
        // Register your endpoints and handle exceptions here.
        serverHandler.createRoutes();
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        System.out.println("Listening on port.");
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
