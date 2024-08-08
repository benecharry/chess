package client;

import websocket.WebSocketFacade;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private SharedUI currentUI;
    private WebSocketFacade webSocketFacade;

    public Repl(String serverUrl) {
        currentUI = new PreLoginUI(serverUrl);
    }

    public void run() {
        System.out.println("\uD83D\uDC51 Welcome to 240 chess. Type Help to get started. \uD83D\uDC51");
        Scanner scanner = new Scanner(System.in);

        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = currentUI.eval(line);
                System.out.print(RESET_TEXT_COLOR + result + RESET_TEXT_COLOR);

                if (result.equals("quit")) {
                    break;
                } else {
                    handleStateTransitions();
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + msg + RESET_TEXT_COLOR);
            }
        }
        System.out.println();
    }

    private void handleStateTransitions() {
        if (currentUI.getState() == State.LOGGEDIN) {
            if (currentUI instanceof PreLoginUI) {
                currentUI = new PostLoginUI(currentUI.getServerUrl(), currentUI.getAuthToken());
            } else if (currentUI instanceof GameplayUI) {
                currentUI = new PostLoginUI(currentUI.getServerUrl(), currentUI.getAuthToken());
            }
        } else if (currentUI.getState() == State.INGAME) {
            if (currentUI instanceof PostLoginUI) {
                PostLoginUI postLoginUI = (PostLoginUI) currentUI;
                try {
                    webSocketFacade = new WebSocketFacade(currentUI.getServerUrl(), postLoginUI);
                    currentUI = new GameplayUI(currentUI.getServerUrl(), currentUI.getAuthToken(), postLoginUI.getPlayerColor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (currentUI.getState() == State.LOGGEDOUT) {
            if (currentUI instanceof PostLoginUI) {
                currentUI = new PreLoginUI(currentUI.getServerUrl());
            }
        }
    }

    private void printPrompt() {
        if (currentUI.getState() == State.LOGGEDOUT) {
            System.out.print(RESET_TEXT_COLOR + "\n[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
        } else if (currentUI.getState() == State.INGAME) {
            System.out.print(RESET_TEXT_COLOR + "\n[IN_GAME] >>> " + SET_TEXT_COLOR_GREEN);
        } else {
            System.out.print(RESET_TEXT_COLOR + "\n[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
        }
    }
}