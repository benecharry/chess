package client;

import exception.ResponseException;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import request.LogoutRequest;
import result.CreateGameResult;
import result.JoinGameResult;
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
                case "join" -> joinGame(params);
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
            resultString.append(String.format("Game ID: %s, White Username: %s, Black Username: %s, Game Name: %s\n",
                    game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return resultString.toString();
    }

    public String joinGame(String... params) throws ResponseException{
        if (params.length == 2) {
            int gameID = Integer.parseInt(params[0]);;
            String playerColor = params[1];
            JoinGameRequest request = new JoinGameRequest(playerColor, gameID, authToken);
            JoinGameResult result = server.joinGame(request);
            return String.format("Joined game with ID: %d as %s.", gameID, playerColor);
        }
        throw new ResponseException(400, "Expected: join <ID> <WHITE|BLACK>");
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}