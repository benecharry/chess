package handler;

import dataaccess.DataAccessException;
import request.ClearApplicationRequest;
import result.ClearApplicationResult;
import service.ClearApplicationService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;

public class ClearApplicationHandler  implements Route {
    private final ClearApplicationService clearApplicationService;

    public ClearApplicationHandler(ClearApplicationService clearApplicationService) {
        this.clearApplicationService = clearApplicationService;
    }

    @Override
    public Object handle(Request req, Response res) {
        try{
            ClearApplicationRequest request = SerializationHandler.fromJson(req.body(), ClearApplicationRequest.class);
            ClearApplicationResult result = clearApplicationService.clearApplication(request);
            res.status(200);
            return SerializationHandler.toJson(result);
        } catch (DataAccessException e){
            //[500] { "message": "Error: (description of error)" }
            HashMap<String, String> errorResult = new HashMap<>();
            res.status(500);
            errorResult.put("message", "Error: " + e.getMessage());
            return SerializationHandler.toJson(errorResult);
        }
    }
}
