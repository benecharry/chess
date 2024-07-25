package handler;

import exception.UnauthorizedException;
import spark.Response;

import java.util.HashMap;

public class ErrorHandler {
    public static String handleException(Exception e, Response res) {
        HashMap<String, String> errorResult = new HashMap<>();
        if (e instanceof UnauthorizedException) {
            res.status(401);
            errorResult.put("message", "Error: unauthorized");
        } else if (e instanceof IllegalArgumentException) {
            res.status(400);
            errorResult.put("message", "Error: bad request");
        } else {
            res.status(500);
            errorResult.put("message", "Error: " + e.getMessage());
        }
        return SerializationHandler.toJson(errorResult);
    }
}