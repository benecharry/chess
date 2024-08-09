package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.InvalidMoveException;
import exception.InvalidParameters;
import exception.ResponseException;
import websocket.GameHandler;
import websocket.messages.ServerMessage;

import javax.websocket.Session;
import javax.websocket.CloseReason;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static ui.EscapeSequences.*;

public class GameplayUI extends SharedUI implements GameHandler {
    private int gameID;
    private ChessGame chessGame;
    private ChessGame.TeamColor playerColor;
    private Collection<ChessPosition> highlightPositions;

    public GameplayUI(String serverUrl, String authToken, ChessGame.TeamColor playerColor, int gameID) {
        super(serverUrl);
        this.state = State.INGAME;
        this.authToken = authToken;
        this.playerColor = playerColor;
        this.chessGame = new ChessGame();
        this.gameID = gameID;
        this.highlightPositions = Collections.emptyList();
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
        clearHighlights();
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        BoardUI.drawChessboard(out, chessGame, playerColor == ChessGame.TeamColor.WHITE, highlightPositions);
        BoardUI.resetColors(out);
        return "You redrew the chessboard.";
    }

    public void clearHighlights() {
        highlightPositions = Collections.emptyList();
    }

    public String leaveGame() throws ResponseException, InvalidParameters {
        ws.leaveGame(authToken, gameID);
        this.setState(State.LOGGEDIN);
        return "You have left the game.";
    }

    public String makeMove(String... params) throws ResponseException, InvalidParameters {
        if(params.length != 2){
            throw new InvalidParameters("You need to provide two directions. Please try again.");
        }

        ChessPosition startPosition = new ChessPosition(params[0]);
        ChessPosition endPosition = new ChessPosition(params[1]);

        ChessPiece piece = chessGame.getBoard().getPiece(startPosition);
        if (piece == null) {
            return "No piece at the giving starting position.";
        }

        ChessMove move = new ChessMove(startPosition, endPosition, null);
        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e) {
            return "Invalid move: " + e.getMessage();
        }

        ws.makeMove(authToken, gameID, move);

        redrawChessBoard();

        return String.format("Moved %s %s from %s to %s.",
                piece.getTeamColor().name().toLowerCase(),
                piece.getPieceType().name().toLowerCase(),
                params[0], params[1]);
    }

    public String resignGame() throws ResponseException, InvalidParameters {
        if (ws == null) {
            throw new IllegalStateException("WebSocket connection is not initialized.");
        }
        ws.resignGame(authToken, gameID);
        this.setState(State.LOGGEDIN);
        return "You have resigned from the game.";
    }

    public String highlightLegalMoves(String... params) throws ResponseException, InvalidParameters {
        if (params.length != 1) {
            throw new InvalidParameters("You can only provide one position. Try again.");
        }

        ChessPosition position = new ChessPosition(params[0]);
        Collection<ChessMove> validMoves = chessGame.validMoves(position);

        highlightPositions = validMoves.stream()
                .map(ChessMove::getEndPosition)
                .collect(Collectors.toList());
        highlightPositions.add(position);

        redrawChessBoard();

        return "Legal moves for piece at " + params[0];
    }

    @Override
    public void onOpen(Session session) {
        System.out.println("GamePlay WebSocket connection opened: " + session.getId());
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
        handleServerMessage(serverMessage);
    }
}
