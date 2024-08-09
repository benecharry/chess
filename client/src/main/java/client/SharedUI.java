package client;

import server.ServerFacade;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public abstract class SharedUI {
    protected State state = State.LOGGEDOUT;
    protected final ServerFacade server;
    protected String authToken;
    protected final String serverUrl;
    protected static WebSocketFacade ws;
    protected static boolean hasJoinedGame;

    public SharedUI(String serverUrl) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
    }

    public abstract String eval(String input);

    public String getAuthToken() {
        return authToken;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String quit() {
        System.out.println("You are exiting the game. Thanks for playing");
        return "quit";
    }

    public String help() {
        switch (state) {
            case LOGGEDOUT:
                return String.format("""
                        %s  register <USERNAME> <PASSWORD> <EMAIL>%s - to create an account
                        %s  login <USERNAME> <PASSWORD>%s - to play chess
                        %s  quit%s - playing chess
                        %s  help%s - with possible commands
                        """,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR);
            case LOGGEDIN:
                return String.format("""
                        %s   create <NAME>%s - a game
                        %s   list%s - games
                        %s   join <ID> <WHITE|BLACK>%s - a game
                        %s   observe <ID>%s - a game
                        %s   logout%s - when you are done
                        %s   quit%s - playing chess
                        %s   help%s - with possible commands
                        """,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR);
            case INGAME:
                return String.format("""
                        %s   redraw%s - the chess board
                        %s   leave%s - the game
                        %s   move <FROM> <TO>%s - make a move
                        %s   resign%s - the game
                        %s   highlight <POSITION>%s - highlight legal moves
                        %s   help%s - with possible commands
                        """,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR,
                        SET_TEXT_COLOR_BLUE, RESET_TEXT_COLOR);
            default:
                return "Invalid state.";
        }
    }

    protected void onGameLoaded(LoadGameMessage loadGameMessage) {
        //Subclasses will override this.
    }

    public void handleServerMessage(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                if (serverMessage instanceof LoadGameMessage) {
                    LoadGameMessage loadGameMessage = (LoadGameMessage) serverMessage;
                    onGameLoaded(loadGameMessage);
                    System.out.println(loadGameMessage.toString());
                }
                break;
            case ERROR:
                if (serverMessage instanceof ErrorMessage) {
                    ErrorMessage errorMessage = (ErrorMessage) serverMessage;
                    System.out.println("Error: " + errorMessage.getErrorMessage());
                }
                break;
            case NOTIFICATION:
                if (serverMessage instanceof NotificationMessage) {
                    NotificationMessage notificationMessage = (NotificationMessage) serverMessage;
                    System.out.println(notificationMessage.toString());
                }
                break;
            default:
                System.out.println("Unknown message type: " + serverMessage);
                break;
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void resetState() {
        this.state = State.LOGGEDOUT;
        this.authToken = null;
    }
}