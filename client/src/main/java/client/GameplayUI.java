package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.InvalidParameters;
import exception.ResponseException;
import websocket.GameHandler;
import websocket.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.Session;
import javax.websocket.CloseReason;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GameplayUI extends SharedUI implements GameHandler {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private ChessGame chessGame;
    private ChessGame.TeamColor playerColor;
    private WebSocketFacade ws;

    public GameplayUI(String serverUrl, String authToken, ChessGame.TeamColor playerColor) {
        super(serverUrl);
        this.state = State.INGAME;
        this.authToken = authToken;
        this.playerColor = playerColor;
        this.chessGame = new ChessGame();
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (cmd) {
                case "help":
                    return help();
                case "redraw":
                    return redrawChessBoard();
                case "leave":
                    return leaveGame();
                case "move":
                    return makeMove(params);
                case "resign":
                    return resignGame();
                case "highlight":
                    return highlightLegalMoves(params);
                default:
                    throw new InvalidParameters(cmd + ". Please try a valid option. Type " + SET_TEXT_COLOR_BLUE +
                            SET_TEXT_BOLD + "'help'" + RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_YELLOW + " to see the menu.");
            }
        } catch (ResponseException ex) {
            return ex.getMessage();
        } catch (InvalidParameters e) {
            return String.format("%sInvalid input: %s%s", SET_TEXT_COLOR_YELLOW, e.getMessage(), RESET_TEXT_COLOR);
        }
    }

    public String redrawChessBoard() throws ResponseException, InvalidParameters {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawChessboard(out, chessGame, playerColor == ChessGame.TeamColor.WHITE);
        resetColors(out);
        return "You redrew the chessboard.";
    }

    public String leaveGame() throws ResponseException, InvalidParameters {
        this.setState(State.LOGGEDIN);
        return "You have left the game.";
    }

    public String makeMove(String... params) throws ResponseException, InvalidParameters {
        // TO-DO
        return "";
    }

    public String resignGame() throws ResponseException, InvalidParameters {
        // TO-DO
        return "";
    }

    public String highlightLegalMoves(String... params) throws ResponseException, InvalidParameters {
        // TO-DO
        return "";
    }

    public static void drawChessboard(PrintStream out, ChessGame chessGame, boolean isPlayerPerspective) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

        drawHeaders(out, isPlayerPerspective);

        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            drawRowLabel(out, row, isPlayerPerspective);
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                drawSquare(out, row, col);
                out.print(getPieceAt(chessGame, row, col, isPlayerPerspective));
            }
            drawRowLabel(out, row, isPlayerPerspective);
            out.println();
        }
        drawHeaders(out, isPlayerPerspective);
    }

    private static void drawHeaders(PrintStream out, boolean isPlayerPerspective) {
        String[] normalHeaders = {" a", "b", "c", "d", "e", "f", "g", "h"};
        String[] altHeaders = {" h", "g", "f", "e", "d", "c", "b", "a"};
        String[] headers = isPlayerPerspective ? normalHeaders : altHeaders;

        out.print(SET_BG_COLOR_DARK_GREY + "   ");
        for (String header : headers) {
            out.print(header + "  ");
        }
        out.print("  " + RESET_BG_COLOR + "\n");
    }

    private static void drawRowLabel(PrintStream out, int row, boolean isPlayerPerspective) {
        out.print(SET_BG_COLOR_DARK_GREY + " " + getRowLabel(row, isPlayerPerspective) + " " + RESET_BG_COLOR);
    }

    private static void drawSquare(PrintStream out, int row, int col) {
        if ((row + col) % 2 == 0) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
        } else {
            out.print(SET_BG_COLOR_BLACK);
        }
    }

    private static String getPieceAt(ChessGame chessGame, int row, int col, boolean isPlayerPerspective) {
        ChessPosition position = isPlayerPerspective
                ? new ChessPosition(BOARD_SIZE_IN_SQUARES - row, col + 1)
                : new ChessPosition(row + 1, BOARD_SIZE_IN_SQUARES - col);

        ChessPiece piece = chessGame.getBoard().getPiece(position);
        if (piece == null) {
            return EMPTY;
        }

        switch (piece.getPieceType()) {
            case KING:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN:
                return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            default:
                return EMPTY;
        }
    }

    private static int getRowLabel(int row, boolean isPlayerPerspective) {
        return isPlayerPerspective ? BOARD_SIZE_IN_SQUARES - row : row + 1;
    }

    private static void resetColors(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    @Override
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket connection closed: " + session.getId() + " Reason: " + closeReason);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        System.err.println("WebSocket error for session " + session.getId() + ": " + thr.getMessage());
        thr.printStackTrace();
    }

    @Override
    public void processMessage(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                if (serverMessage instanceof LoadGameMessage) {
                    LoadGameMessage loadGameMessage = (LoadGameMessage) serverMessage;
                    System.out.println("Game loaded: " + loadGameMessage.getGame());
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
                    System.out.println("Notification: " + notificationMessage.getMessage());
                }
                break;
        }
    }
}