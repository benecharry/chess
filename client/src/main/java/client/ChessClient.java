package client;

import exception.ResponseException;
import request.RegisterRequest;
import result.RegisterResult;
import server.ServerFacade;
import static ui.EscapeSequences.*;

import java.util.Arrays;

public class ChessClient {
    private State state = State.LOGGEDOUT;
    private final ServerFacade server;
    private final PreloginUI preloginUI;
    private String authToken = null;

    public ChessClient(String serverUrl, PreloginUI preloginUI) {
        this.server = new ServerFacade(serverUrl);
        this.preloginUI = preloginUI;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException{
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

    public String quit(){
        return "quit";
    }

    public String help() {
        if (state == State.LOGGEDOUT) {
            return String.format("""
                    %sregister <USERNAME> <PASSWORD> <EMAIL>%s - to create an account
                    %slogin <USERNAME> <PASSWORD>%s - to play chess
                    %squit%s - playing chess
                    %shelp%s - with possible commands
                    """,
                    SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                    SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                    SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                    SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR);
        }
        return String.format("""
                %screate <NAME>%s - a game
                %slist%s - games
                %sjoin <ID> <WHITE|BLACK>%s - a game
                %sobserve <ID>%s - a game
                %slogout%s - when you are done
                %squit%s - playing chess
                %shelp%s - with possible commands
                """,
                SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR);
    }

    public State getState() {
        return state;
    }
}
