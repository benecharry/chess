package client;

import chess.ChessGame;
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
import server.ServerFacade;
import websocket.GameHandler;
import websocket.WebSocketFacade;
import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;

import javax.websocket.Session;
import javax.websocket.CloseReason;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import static ui.EscapeSequences.*;

public class PostLoginUI extends SharedUI implements GameHandler {
    private HashMap<Integer, Integer> localGameIDs;
    private int nextLocalID;
    private ChessGame.TeamColor playerColor;
    private ServerFacade server;
    private int currentGameID;
    private boolean hasJoinedGame;

    public PostLoginUI(String serverUrl, String authToken) {
        super(serverUrl);
        this.authToken = authToken;
        this.state = State.LOGGEDIN;
        this.localGameIDs = new HashMap<>();
        this.nextLocalID = 1;
        server = new ServerFacade(serverUrl);
        hasJoinedGame = false;
        initializeLocalGameIDs();
    }

    private void initializeLocalGameIDs() {
        try {
            ListGamesRequest request = new ListGamesRequest(authToken);
            ListGamesResult result = server.listGames(request);
            for (ListGamesResult.GameDetails game : result.games()) {
                localGameIDs.put(nextLocalID, game.gameID());
                nextLocalID++;
            }
        } catch (ResponseException e) {
            System.out.println("Error initializing client to server IDs: " + e.getMessage());
        }
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
                            SET_TEXT_BOLD + "'help'" + RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_YELLOW + " to see the menu.");
            }
        } catch (ResponseException | IOException ex) {
            return ex.getMessage();
        } catch (InvalidParameters e) {
            return String.format("%sInvalid input: %s%s", SET_TEXT_COLOR_YELLOW, e.getMessage(), RESET_TEXT_COLOR);
        } catch (NumberFormatException e) {
            return String.format("%sInvalid input. Game <ID> should be a number.%s", SET_TEXT_COLOR_YELLOW, RESET_TEXT_COLOR);
        } catch (NullPointerException | IllegalArgumentException e) {
            return String.format("%sInvalid input. Try again.%s", SET_TEXT_COLOR_YELLOW, RESET_TEXT_COLOR);
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
            int localID = nextLocalID++;
            localGameIDs.put(localID, result.gameID());
            return String.format("Game '%s' created with ID: %d", gameName, localID);
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
        localGameIDs.clear();
        nextLocalID = 1;

        if (result.games().isEmpty()) {
            return "No games have been created yet.";
        }

        System.out.println(SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD + "ID" + RESET_TEXT_COLOR);
        for (ListGamesResult.GameDetails game : result.games()) {
            localGameIDs.put(nextLocalID, game.gameID());
            resultString.append(String.format("%d. Game name: %s\n      White player: %s\n      Black player: %s\n",
                    nextLocalID++, game.gameName(), game.whiteUsername(), game.blackUsername()));
        }
        return resultString.toString();
    }

    public String joinGame(String... params) throws ResponseException, InvalidParameters, IOException {
        assertSignedIn();
        if (params.length == 2) {
            int clientGameID = Integer.parseInt(params[0]);
            String playerColor = params[1];
            this.playerColor = ChessGame.TeamColor.valueOf(playerColor.toUpperCase());
            Integer databaseGameID = localGameIDs.get(clientGameID);

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

            if (ws == null) {
                ws = new WebSocketFacade(serverUrl, this);
                ws.connect(authToken, databaseGameID);
            }

            this.currentGameID = databaseGameID;
            this.setState(State.INGAME);
            return "";
        }
        throw new InvalidParameters("Try again by typing " + SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD + "'join'" +
                RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_YELLOW + ", followed by the " + SET_TEXT_BOLD + "<ID>" +
                RESET_TEXT_BOLD_FAINT + " of the game and the team you want to join "  + SET_TEXT_BOLD +
                "<white>" + RESET_TEXT_BOLD_FAINT + " or " + SET_TEXT_BOLD + "<black>" + RESET_TEXT_BOLD_FAINT + ".");
    }

    public String observeGame(String... params) throws ResponseException, InvalidParameters, IOException {
        assertSignedIn();
        if (params.length == 1) {
            int clientGameID = Integer.parseInt(params[0]);
            Integer databaseGameID = localGameIDs.get(clientGameID);
            if (databaseGameID == null) {
                throw new InvalidParameters("Game ID not found.");
            }

            if (ws == null) {
                ws = new WebSocketFacade(serverUrl, this);
                ws.connect(authToken, databaseGameID);
            }

            this.currentGameID = databaseGameID;
            this.setState(State.INGAME);
            return "";
        }
        throw new InvalidParameters("Try again by typing " + SET_TEXT_COLOR_BLUE + SET_TEXT_BOLD + "'observe'" +
                RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_YELLOW + " followed by the " + SET_TEXT_BOLD + "<ID>" +
                RESET_TEXT_BOLD_FAINT + " of the game you want to observe");
    }

    private void assertSignedIn() throws ResponseException, InvalidParameters {
        if (state == State.LOGGEDOUT) {
            throw new InvalidParameters("You must sign in first.");
        }
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public int getCurrentGameID() {
        return currentGameID;
    }

    @Override
    public void onOpen(Session session) {
        //System.out.println("Succesfully connected to the game: " + session.getId());
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("\nConnection to the game closed. Thanks for playing.");
    }

    @Override
    public void onError(Session session, Throwable thr) {
        //
    }

    @Override
    public void processMessage(ServerMessage serverMessage) {
        handleServerMessage(serverMessage);
    }
}