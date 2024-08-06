package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class GameplayUI {

    private static final int BOARD_SIZE_IN_SQUARES = 8;

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        ChessGame chessGame = new ChessGame();
        drawInitialBoardState(out, chessGame, ChessGame.TeamColor.WHITE);

        resetColors(out);
    }

    //Only for the initial board.

    public static void drawInitialBoardState(PrintStream out, ChessGame chessGame, ChessGame.TeamColor playerColor) {
        out.print(ERASE_SCREEN);
        //drawChessboard(out, chessGame, false);
        //out.print(SET_BG_COLOR_BLACK + "                              " + RESET_BG_COLOR + "\n");
        out.println();
        drawChessboard(out, chessGame, playerColor == ChessGame.TeamColor.WHITE);
        resetColors(out);
    }

    public static void drawChessboard(PrintStream out, ChessGame chessGame, boolean isNormalPerspective) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

        drawHeaders(out, isNormalPerspective);

        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            drawRowLabel(out, row, isNormalPerspective);
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                drawSquare(out, row, col);
                out.print(getPieceAt(chessGame, row, col, isNormalPerspective));
            }
            drawRowLabel(out, row, isNormalPerspective);
            out.println();
        }

        drawHeaders(out, isNormalPerspective);
    }

    private static void drawHeaders(PrintStream out, boolean isNormalPerspective) {
        String[] normalHeaders = {" a", "b", "c", "d", "e", "f", "g", "h"};
        String[] altHeaders = {" h", "g", "f", "e", "d", "c", "b", "a"};
        String[] headers = isNormalPerspective ? normalHeaders : altHeaders;

        out.print(SET_BG_COLOR_DARK_GREY + "   ");
        for (String header : headers) {
            out.print(header + "  ");
        }
        out.print("  " + RESET_BG_COLOR + "\n");
    }

    private static void drawRowLabel(PrintStream out, int row, boolean isNormalPerspective) {
        out.print(SET_BG_COLOR_DARK_GREY + " " + getRowLabel(row, isNormalPerspective) + " " + RESET_BG_COLOR);
    }

    private static void drawSquare(PrintStream out, int row, int col) {
        if ((row + col) % 2 == 0) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
        } else {
            out.print(SET_BG_COLOR_BLACK);
        }
    }

    private static String getPieceAt(ChessGame chessGame, int row, int col, boolean isNormal) {
        ChessPosition position = isNormal
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

    private static int getRowLabel(int row, boolean isNormal) {
        return isNormal ? BOARD_SIZE_IN_SQUARES - row : row + 1;
    }

    private static void resetColors(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }
}
