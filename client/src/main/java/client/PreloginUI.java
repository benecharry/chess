package client;


import java.util.Scanner;
import static ui.EscapeSequences.*;

public class PreloginUI {
    private final ChessClient client;

    public PreloginUI(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run(){
        System.out.println("\uD83D\uDC51 Welcome to 240 chess. Type Help to get started. \uD83D\uDC51");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if (result.equals("quit")) {
                    break;
                }
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + msg + RESET_TEXT_COLOR);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        if (client.getState() == State.LOGGEDOUT) {
            System.out.print("\n[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
        } else {
            System.out.print("\n[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
        }
    }
}
