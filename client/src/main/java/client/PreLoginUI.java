package client;

import exception.ResponseException;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import exception.InvalidParameters;
import static ui.EscapeSequences.*;

import java.util.Arrays;

public class PreLoginUI extends SharedUI {

    public PreLoginUI(String serverUrl) {
        super(serverUrl);
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (cmd) {
                case "register":
                    return register(params);
                case "login":
                    return login(params);
                case "quit":
                    return quit();
                case "help":
                    return help();
                default:
                    throw new InvalidParameters(cmd + ". Please try a valid option. Type " + SET_TEXT_COLOR_BLUE +
                            SET_TEXT_BOLD + "'help'" + RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_YELLOW + " to see the menu." );
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        } catch (InvalidParameters e) {
            return String.format("%sInvalid input: %s%s", SET_TEXT_COLOR_YELLOW, e.getMessage(), RESET_TEXT_COLOR);
        }
    }


    public String register(String... params) throws ResponseException, InvalidParameters {
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
        throw new InvalidParameters("Try again by typing " + SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD +
                "'register'" + RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_YELLOW + ", then your " + SET_TEXT_BOLD + "<username>" +
                RESET_TEXT_BOLD_FAINT + ", followed by your " + SET_TEXT_BOLD + "<password>" + RESET_TEXT_BOLD_FAINT +
                " and " + SET_TEXT_BOLD + "<email>" + RESET_TEXT_BOLD_FAINT + ".");
    }

    public String login(String... params) throws ResponseException, InvalidParameters {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(username, password);
            LoginResult result = server.login(request);
            authToken = result.authToken();
            state = State.LOGGEDIN;
            return String.format("Logged in as %s.", username);
        }
        throw new InvalidParameters("Try again by typing " + SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD +
                "'login'" + RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_YELLOW + " then your " + SET_TEXT_BOLD +
                "<username>" + RESET_TEXT_BOLD_FAINT + " followed by your " + SET_TEXT_BOLD + "<password>" +
                RESET_TEXT_BOLD_FAINT + ".");
    }
}