package client;

import exception.ResponseException;
import request.CreateGameRequest;
import result.CreateGameResult;

import java.util.Arrays;

public class PostloginUI extends SharedUI {

    public PostloginUI(String serverUrl, String authToken) {
        super(serverUrl);
        this.authToken = authToken;
        this.state = State.LOGGEDIN;
    }
    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "quit" -> quit();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            String gameName = params[0];
            CreateGameRequest request = new CreateGameRequest(gameName, authToken);
            CreateGameResult result = server.createGame(request);
            return String.format("Game '%s' created with ID: %d", gameName, result.gameID());
        }
        throw new ResponseException(400, "Expected: create <gameName>");
    }

    public String listGames() {
        return "";
    }
}