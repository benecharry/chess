package handler;

import dataaccess.DataAccessException;
import exception.AlreadyTakenException;
import request.RegisterRequest;
import result.RegisterResult;
import service.RegisterService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;


public class RegisterHandler implements Route {
    private final RegisterService registerService;

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    public Object handle(Request req, Response res){
        try{
            RegisterRequest request = SerializationHandler.fromJson(req.body(), RegisterRequest.class);
            RegisterResult result = registerService.register(request);
            res.status(200);
            return SerializationHandler.toJson(result);
        } catch (IllegalArgumentException e) {
            //Failure response	[400] { "message": "Error: bad request" }
            HashMap<String, String> errorResult = new HashMap<>();
            res.status(400);
            errorResult.put("message", "Error: bad request");
            return SerializationHandler.toJson(errorResult);
        } catch (AlreadyTakenException e) {
            //[403] { "message": "Error: already taken" }
            HashMap<String, String> errorResult = new HashMap<>();
            res.status(403);
            errorResult.put("message", "Error: already taken");
            return SerializationHandler.toJson(errorResult);
        } catch (DataAccessException e){
            //[500] { "message": "Error: (description of error)" }
            HashMap<String, String> errorResult = new HashMap<>();
            res.status(500);
            errorResult.put("message", "Error: " + e.getMessage());
            return SerializationHandler.toJson(errorResult);
        }
    }
}

