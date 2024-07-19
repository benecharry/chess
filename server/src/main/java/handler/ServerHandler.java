package handler;

import manager.ServiceManager;
import spark.Spark;

public class ServerHandler {
    private final ServiceManager serviceManager;

    public ServerHandler(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public  void createRoutes() {
        var registerService = serviceManager.getRegisterService();
        Spark.post("/user", new RegisterHandler(registerService));
        var clearApplicationService = serviceManager.getClearApplicationService();
        Spark.delete("/db", new ClearApplicationHandler(clearApplicationService));
        var loginService = serviceManager.getLoginService();
        Spark.post("/session", new LoginHandler(loginService));
        var logoutService = serviceManager.getLogoutService();
        Spark.delete("/session", new LogoutHandler(logoutService));
    }
}
