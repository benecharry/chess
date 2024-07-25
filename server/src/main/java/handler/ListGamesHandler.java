package handler;

import request.ListGamesRequest;
import result.ListGamesResult;
import service.ListGamesService;
import spark.Request;
import spark.Response;
import spark.Route;

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
        } catch (Exception e) {
            return ErrorHandler.handleException(e, res);
        }
    }
}