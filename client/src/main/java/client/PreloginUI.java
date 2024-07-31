package client;

import exception.ResponseException;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.Arrays;

public class PreloginUI extends SharedUI {

    public PreloginUI(String serverUrl) {
        super(serverUrl);
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> quit();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            RegisterRequest request = new RegisterRequest(username, password, email);
            RegisterResult result = server.register(request);
            authToken = result.authToken();
            state = State.LOGGEDIN;
            return String.format("Registered and logged in as %s.", username);
        }
        throw new ResponseException(400, "Expected: register <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(username, password);
            LoginResult result = server.login(request);
            authToken = result.authToken();
            state = State.LOGGEDIN;
            return String.format("Logged in as %s.", username);
        }
        throw new ResponseException(400, "Expected: login <username> <password>");
    }
}