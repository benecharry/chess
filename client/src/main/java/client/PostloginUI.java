package client;

import exception.InvalidParameters;
import exception.ResponseException;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import request.LogoutRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;
import result.LogoutResult;
import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.HashMap;

public class PostloginUI extends SharedUI {
    private HashMap<Integer, Integer> clientToServerGameIDs;

    public PostloginUI(String serverUrl, String authToken) {
        super(serverUrl);
        this.authToken = authToken;
        this.state = State.LOGGEDIN;
        this.clientToServerGameIDs = new HashMap<>();
    }
    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (cmd) {
                case "logout":
                    return logout();
                case "create":
                    return createGame(params);
                case "list":
                    return listGames();
                case "join":
                    return joinGame(params);
                case "observe":
                    return observeGame(params);
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
        } catch (NumberFormatException e) {
            return String.format("%sInvalid input. Game <ID> should be a number.%s", SET_TEXT_COLOR_YELLOW, RESET_TEXT_COLOR);
        } catch (NullPointerException e) {
            return String.format("%sInvalid input. Game <ID> not found.%s", SET_TEXT_COLOR_YELLOW, RESET_TEXT_COLOR);
        }
    }

    public String logout() throws ResponseException, InvalidParameters {
        assertSignedIn();
        String token = getAuthToken();
        LogoutRequest request = new LogoutRequest(token);
        LogoutResult result = server.logout(request);
        resetState();
        return "Logout request was successful. Thanks for playing!";
    }

    public String createGame(String... params) throws ResponseException, InvalidParameters {
        assertSignedIn();
        if (params.length == 1) {
            String gameName = params[0];
            CreateGameRequest request = new CreateGameRequest(gameName, authToken);
            CreateGameResult result = server.createGame(request);
            return String.format("Game '%s' created with ID: %d", gameName, result.gameID());
        }
        throw new InvalidParameters("Try again by typing " + SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD + "'create'"
                + RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_YELLOW + " followed by the " + SET_TEXT_BOLD + "<name>"
                + RESET_TEXT_BOLD_FAINT + " you want to give to your new name.");
    }

    public String listGames() throws ResponseException, InvalidParameters {
        assertSignedIn();
        ListGamesRequest request = new ListGamesRequest(authToken);
        ListGamesResult result = server.listGames(request);

        StringBuilder resultString = new StringBuilder();
        clientToServerGameIDs.clear();
        int index = 1;
        System.out.println("ID");
        for (ListGamesResult.GameDetails game : result.games()) {
            clientToServerGameIDs.put(index, game.gameID());

            resultString.append(String.format("%d. Name: %s\n     White Player: %s\n     Black Player: %s\n",
                    index++, game.gameName(), game.whiteUsername(), game.blackUsername()));
        }
        return resultString.toString();
    }

    //Make it to have local ID. DONE
    //Join game also needs to know about these IDs. DONE
    //Missing parameters. DONE
    //Wrong parameters. DONE
    //Unit tests to auto-grader. First
    //Queue with TAs.

    public String joinGame(String... params) throws ResponseException, InvalidParameters {
        assertSignedIn();
        if (params.length == 2) {
            int clientGameID = Integer.parseInt(params[0]);;
            String playerColor = params[1];
            Integer databaseGameID = clientToServerGameIDs.get(clientGameID);
            if (databaseGameID == null) {
                throw new InvalidParameters("Game ID not found.");
            }

            ListGamesRequest listRequest = new ListGamesRequest(authToken);
            ListGamesResult listResult = server.listGames(listRequest);
            ListGamesResult.GameDetails gameDetails = null;

            for (ListGamesResult.GameDetails game : listResult.games()) {
                if (game.gameID() == databaseGameID) {
                    gameDetails = game;
                    break;
                }
            }

            if (gameDetails == null) {
                throw new InvalidParameters("Game not found.");
            }

            if ((playerColor.equalsIgnoreCase("white") && gameDetails.whiteUsername() != null) ||
                    (playerColor.equalsIgnoreCase("black") && gameDetails.blackUsername() != null)) {
                throw new InvalidParameters("The " + playerColor + " team is already taken. Please select another game or team.");
            }

            JoinGameRequest request = new JoinGameRequest(playerColor, databaseGameID, authToken);
            JoinGameResult result = server.joinGame(request);
            return String.format("You have joined the game with ID: %d as %s.", clientGameID, playerColor);
        }
        throw new InvalidParameters("Try again by typing " + SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD + "'join'" +
                RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_YELLOW + ", followed by the " + SET_TEXT_BOLD + "<ID>" +
                RESET_TEXT_BOLD_FAINT + " of the game and the team you want to join "  + SET_TEXT_BOLD +
                "<white>" + RESET_TEXT_BOLD_FAINT + " or " + SET_TEXT_BOLD + "<black>" + RESET_TEXT_BOLD_FAINT + ".");
    }

    public String observeGame(String... params) throws ResponseException, InvalidParameters {
        assertSignedIn();
        if (params.length == 1) {
            int clientGameID = Integer.parseInt(params[0]);;
            Integer databaseGameID = clientToServerGameIDs.get(clientGameID);
            if (databaseGameID == null) {
                throw new InvalidParameters("Game ID not found.");
            }
            return server.observeGame(databaseGameID);
        }
        throw new InvalidParameters("Try again by typing " + SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD + "'join'" +
                RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_YELLOW + " followed by the " + SET_TEXT_BOLD + "<ID>" +
                RESET_TEXT_BOLD_FAINT + " of the game you want to observe");
    }

    private void assertSignedIn() throws ResponseException, InvalidParameters {
        if (state == State.LOGGEDOUT) {
            throw new InvalidParameters("You must sign in first.");
        }
    }
}