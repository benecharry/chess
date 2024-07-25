package handler;

import request.CreateGameRequest;
import result.CreateGameResult;
import service.CreateGameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private final CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService){this.createGameService = createGameService;}

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            String authToken = req.headers("authorization");
            String body = req.body();
            CreateGameRequest request = SerializationHandler.fromJson(body, CreateGameRequest.class);
            request = new CreateGameRequest(request.gameName(), authToken);
            CreateGameResult result = createGameService.createGame(request);
            res.status(200);
            return SerializationHandler.toJson(result);
        } catch (Exception e) {
            return ErrorHandler.handleException(e, res);
        }
    }
}
