package handler;

import exception.ResponseException;
import manager.ServiceManager;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.servlet.SparkApplication;

public class ServerHandler {
    private final ServiceManager serviceManager;

    public ServerHandler(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public  void createRoutes() {
        var registerService = serviceManager.getRegisterService();
        Spark.post("/user", new RegisterHandler(registerService));
        var loginService = serviceManager.getLoginService();
        Spark.post("/session", new LoginHandler(loginService));
        var logoutService = serviceManager.getLogoutService();
        Spark.delete("/session", new LogoutHandler(logoutService));
        var listGamesService = serviceManager.getListGamesService();
        Spark.get("/game", new ListGamesHandler(listGamesService));
        var createGameService = serviceManager.getCreateGameService();
        Spark.post("/game", new CreateGameHandler(createGameService));
        var joinGameService = serviceManager.getJoinGameService();
        Spark.put("/game", new JoinGameHandler(joinGameService));
        var clearApplicationService = serviceManager.getClearApplicationService();
        Spark.delete("/db", new ClearApplicationHandler(clearApplicationService));
        Spark.exception(ResponseException.class, this::exceptionHandler);
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.statusCode());
    }

}
