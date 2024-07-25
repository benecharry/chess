package handler;

import exception.UnauthorizedException;
import request.LoginRequest;
import result.LoginResult;
import service.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;

public class LoginHandler implements Route {
    private final LoginService loginService;
    public LoginHandler(LoginService loginService){
        this.loginService = loginService;
    }
    @Override
    public Object handle(Request req, Response res) throws Exception {
        try{
            LoginRequest request = SerializationHandler.fromJson(req.body(), LoginRequest.class);
            LoginResult result = loginService.login(request);
            res.status(200);
            return SerializationHandler.toJson(result);
        } catch(UnauthorizedException e) {
            //Failure response	[401] { "message": "Error: unauthorized" }
            HashMap<String, String> errorResult = new HashMap<>();
            res.status(401);
            errorResult.put("message", "Error: unauthorized");
            return SerializationHandler.toJson(errorResult);
        } catch(Exception e){
            //Failure response	[500] { "message": "Error: (description of error)" }}
            HashMap<String, String> errorResult = new HashMap<>();
            res.status(500);
            errorResult.put("message", "Error: " + e.getMessage());
            return SerializationHandler.toJson(errorResult);
        }
    }
}
