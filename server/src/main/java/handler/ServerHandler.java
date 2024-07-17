package handler;

import service.RegisterService;
import spark.Spark;

public class ServerHandler {
    private final RegisterService registerService;

    public ServerHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    public static void createRoutes() {
        Spark.post("/user", new RegisterHandler());
    }
}
