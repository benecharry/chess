package handler;

import exception.UnauthorizedException;
import request.ListGamesRequest;
import result.ListGamesResult;
import service.ListGamesService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;

public class ListGamesHandler implements Route {
    private final ListGamesService listGamesService;

    public ListGamesHandler(ListGamesService listGamesService){
        this.listGamesService = listGamesService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception{
        try{
            ListGamesRequest request = new ListGamesRequest(req.headers("authorization"));
            ListGamesResult result = listGamesService.listgames(request);
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
