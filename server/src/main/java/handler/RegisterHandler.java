package handler;

import dataaccess.DataAccessException;
import exception.AlreadyTakenException;
import request.RegisterRequest;
import result.RegisterResult;
import service.RegisterService;
import spark.Request;
import spark.Response;
import spark.Route;


public class RegisterHandler implements Route {
    private final RegisterService registerService;

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    public Object handle(Request req, Response res) throws DataAccessException, AlreadyTakenException {
        RegisterRequest request = SerializationHandler.fromJson(req.body(), RegisterRequest.class);
        RegisterResult result = registerService.register(request);

        res.status(200);

        return SerializationHandler.toJson(result);
    }
}

