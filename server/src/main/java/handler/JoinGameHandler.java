package handler;


import exception.AlreadyTakenException;
import spark.Request;
import spark.Response;
import service.JoinGameService;
import spark.Route;
import request.JoinGameRequest;
import result.JoinGameResult;

import java.util.HashMap;

public class JoinGameHandler implements Route {
    private final JoinGameService joinGameService;
    public JoinGameHandler(JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
    }
    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            String authToken = req.headers("authorization");
            JoinGameRequest request = SerializationHandler.fromJson(req.body(), JoinGameRequest.class);
            JoinGameRequest joinGameRequest = new JoinGameRequest(request.playerColor(), request.gameID(), authToken);
            JoinGameResult result = joinGameService.joingame(joinGameRequest);
            res.status(200);
            return SerializationHandler.toJson(result);
        }catch (AlreadyTakenException e) {
            HashMap<String, String> errorResult = new HashMap<>();
            res.status(403);
            errorResult.put("message", "Error: already taken");
            return SerializationHandler.toJson(errorResult);
        } catch (Exception e) {
            return ErrorHandler.handleException(e, res);
        }
    }
}