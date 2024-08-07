package server;

import dataaccess.*;
import dataaccess.DataAccessException;
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
        this(startServiceManager());
    }

    public Server(ServiceManager serviceManager) {
        this.serverHandler = new ServerHandler(serviceManager);
    }

    private static ServiceManager startServiceManager() {
        try {
            DatabaseInitializer dbInitializer = new DatabaseInitializer();
            dbInitializer.configureDatabase();
            System.out.println("Database created.");

            return new ServiceManager(new UserDataSQLDataAccess(), new AuthDataSQLDataAccess(), new GameDataSQLDataAccess());
        } catch (DataAccessException e) {
            throw new RuntimeException("Error initializing the database", e);
        }
    }


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        //TO-DO
        // Register your endpoints and handle exceptions here.
        serverHandler.createRoutes();
        //This line initializes the server and can be removed once you have a functioning endpoint
        //TO-DO
        // Check if the databse already exist.
        //Make sure all tables are in there. Create if not exist table and or database.

        Spark.awaitInitialization();
        return Spark.port();
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
