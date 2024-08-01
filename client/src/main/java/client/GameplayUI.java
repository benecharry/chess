package client;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class GameplayUI {

    private static final int BOARD_SIZE_IN_SQUARES = 8;

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawChessboard(out);
        drawChessboardAltOrientation(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public static void drawChessboard(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

        out.print(SET_BG_COLOR_DARK_GREY + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n");

        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            out.print(SET_BG_COLOR_DARK_GREY + " " + (BOARD_SIZE_IN_SQUARES - row) + " " + RESET_BG_COLOR);
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if ((row + col) % 2 == 0) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                }
                out.print(getPieceAt(row, col, true));
            }
            out.print(SET_BG_COLOR_DARK_GREY + " " + (BOARD_SIZE_IN_SQUARES - row) + " " + RESET_BG_COLOR + "\n");
        }

        out.print(SET_BG_COLOR_DARK_GREY + "    a  b  c  d  e  f  g  h    " + RESET_BG_COLOR + "\n");
    }

    public static void drawChessboardAltOrientation(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

        out.print(SET_BG_COLOR_DARK_GREY + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + "\n");

        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            out.print(SET_BG_COLOR_DARK_GREY + " " + (row + 1) + " " + RESET_BG_COLOR);
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                if ((row + col) % 2 == 0) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                }
                out.print(getPieceAt(row, col, false));
            }
            out.print(SET_BG_COLOR_DARK_GREY + " " + (row + 1) + " " + RESET_BG_COLOR + "\n");
        }

        out.print(SET_BG_COLOR_DARK_GREY + "    h  g  f  e  d  c  b  a    " + RESET_BG_COLOR + "\n");
        out.print(SET_BG_COLOR_BLACK + "                              " + RESET_BG_COLOR + "\n");
    }

    private static String getPieceAt(int row, int col, boolean isNormal) {
        if (isNormal) {
            if (row == 0) {return new String[]{WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN,
                    WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}[col];}
            if (row == 1) {return WHITE_PAWN;}
            if (row == 6) {return BLACK_PAWN;}
            if (row == 7) {return new String[]{BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN,
                    BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK}[col];}
        } else {
            if (row == 0) {return new String[]{BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING,
                    BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK}[col];}
            if (row == 1) {return BLACK_PAWN;}
            if (row == 6) {return WHITE_PAWN;}
            if (row == 7) {return new String[]{WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING,
                    WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK}[col];}
        }
        return EMPTY;
    }

    //ChessBoard print object. Refactor so takes the chessboard.


    public static String getInitialBoardState() {
        var out = new ByteArrayOutputStream();
        var printStream = new PrintStream(out, true, StandardCharsets.UTF_8);
        drawChessboardAltOrientation(printStream);
        drawChessboard(printStream);
        return out.toString(StandardCharsets.UTF_8);
    }
}