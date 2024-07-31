package client;

import exception.ResponseException;
import request.CreateGameRequest;
import request.ListGamesRequest;
import request.LogoutRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LogoutResult;

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
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "quit" -> quit();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws ResponseException{
        assertSignedIn();
        String token = getAuthToken();
        LogoutRequest request = new LogoutRequest(token);
        LogoutResult result = server.logout(request);
        state = State.LOGGEDOUT;
        return "Logout request was successful. Thanks for playing";
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            String gameName = params[0];
            CreateGameRequest request = new CreateGameRequest(gameName, authToken);
            CreateGameResult result = server.createGame(request);
            return String.format("Game '%s' created with ID: %d", gameName, result.gameID());
        }
        throw new ResponseException(400, "Expected: create <gameName>");
    }

    public String listGames() throws ResponseException{
        assertSignedIn();
        ListGamesRequest request = new ListGamesRequest(authToken);
        ListGamesResult result = server.listGames(request);

        StringBuilder resultString = new StringBuilder();
        for (ListGamesResult.GameDetails game : result.games()) {
            resultString.append(String.format("Game ID: %s, White: %s, Black: %s, Name: %s\n",
                    game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return resultString.toString();
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}