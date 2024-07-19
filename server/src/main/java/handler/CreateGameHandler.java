package handler;

import exception.UnauthorizedException;
import request.CreateGameRequest;
import result.CreateGameResult;
import service.CreateGameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;

public class CreateGameHandler implements Route {
    private final CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService){this.createGameService = createGameService;}

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try{
            String authToken = req.headers("authorization");
            CreateGameRequest request = new CreateGameRequest(req.body(), authToken);
            CreateGameResult result = createGameService.createGame(request);
            res.status(200);
            return SerializationHandler.toJson(result);
        } catch (IllegalArgumentException e) {
            //[400] { "message": "Error: bad request" }
            HashMap<String, String> errorResult = new HashMap<>();
            res.status(400);
            errorResult.put("message", "Error: bad request");
            return SerializationHandler.toJson(errorResult);
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
