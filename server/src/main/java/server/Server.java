package server;

import com.sun.net.httpserver.HttpServer;
import dataaccess.AuthDataMemoryDataAccess;
import dataaccess.UserDataMemoryDataAccess;
import handler.RegisterHandler;
import service.RegisterService;
import spark.*;

public class Server {
    //Server looks to the url path and forward it to the exact handler.
    //Request type!
    //Needs to parse the info and put it in an object call Request and sends it to the handler
    // process it and construct the response object that is translated to an HTTP object.

    public int run(int desiredPort) {
        System.out.println("Initializing port.");
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        //TO-DO
        // Register your endpoints and handle exceptions here.
        var userDataDataAccess = new UserDataMemoryDataAccess();
        var authDataDataAccess = new AuthDataMemoryDataAccess();
        var registerService = new RegisterService(userDataDataAccess, authDataDataAccess);
        var registerHandler = new RegisterHandler(registerService);


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
