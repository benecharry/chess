package handler;

import exception.UnauthorizedException;
import request.LogoutRequest;
import result.LogoutResult;
import service.LogoutService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;

public class LogoutHandler implements Route {
    private final LogoutService logoutService;
    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }
    @Override
    public Object handle(Request req, Response res) throws Exception {
        try{
            LogoutRequest request = new LogoutRequest(req.headers("authorization"));
            LogoutResult result = logoutService.logout(request);
            res.status(200);
            return SerializationHandler.toJson(result);
        } catch(UnauthorizedException e) {
            //[401] { "message": "Error: unauthorized" }
            HashMap<String, String> errorResult = new HashMap<>();
            res.status(401);
            errorResult.put("message", "Error: unauthorized");
            return SerializationHandler.toJson(errorResult);
        } catch(Exception e){
            //[500] { "message": "Error: (description of error)" }
            HashMap<String, String> errorResult = new HashMap<>();
            res.status(500);
            errorResult.put("message", "Error: " + e.getMessage());
            return SerializationHandler.toJson(errorResult);
        }
    }
}
