package client;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private SharedUI currentUI;

    public Repl(String serverUrl) {
        currentUI = new PreloginUI(serverUrl);
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
                } else if (currentUI instanceof PreloginUI && currentUI.getState() == State.LOGGEDIN) {
                    currentUI = new PostloginUI(currentUI.getServerUrl(), currentUI.getAuthToken());
                    //System.out.print(currentUI.help());
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + msg + RESET_TEXT_COLOR);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        if (currentUI.getState() == State.LOGGEDOUT) {
            System.out.print(RESET_TEXT_COLOR + "\n[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
        } else {
            System.out.print(RESET_TEXT_COLOR + "\n[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
        }
    }
}