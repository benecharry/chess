package client;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.util.Collection;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class BoardUI {
    private static final int BOARD_SIZE_IN_SQUARES = 8;

    public static void drawChessboard(PrintStream out, ChessGame chessGame, boolean isPlayerPerspective,
                                      Collection<ChessPosition> highlightPositions) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

        drawHeaders(out, isPlayerPerspective);

        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            drawRowLabel(out, row, isPlayerPerspective);
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
                ChessPosition currentPosition = calculatePosition(row, col, isPlayerPerspective);

                boolean isHighlighted = highlightPositions.contains(currentPosition);
                ChessPiece piece = chessGame.getBoard().getPiece(currentPosition);
                boolean isPieceLocation = false;
                if (piece != null) {
                    isPieceLocation = highlightPositions.contains(currentPosition);
                }
                drawSquare(out, row, col, isHighlighted, isPieceLocation);
                out.print(getPieceAt(chessGame, row, col, isPlayerPerspective));
            }
            drawRowLabel(out, row, isPlayerPerspective);
            out.println();
        }
        drawHeaders(out, isPlayerPerspective);
    }

    private static ChessPosition calculatePosition(int row, int col, boolean isPlayerPerspective) {
        int boardRow;
        int boardCol;

        if (isPlayerPerspective) {
            boardRow = BOARD_SIZE_IN_SQUARES - row;
            boardCol = col + 1;
        } else {
            boardRow = row + 1;
            boardCol = BOARD_SIZE_IN_SQUARES - col;
        }

        return new ChessPosition(boardRow, boardCol);
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

    private static void drawSquare(PrintStream out, int row, int col, boolean isHighlighted, boolean isPieceLocation) {
        if (isPieceLocation) {
            out.print(SET_BG_COLOR_YELLOW);
        } else if (isHighlighted) {
            if ((row + col) % 2 == 0) {
                out.print(SET_BG_COLOR_GREEN);
            } else {
                out.print(SET_BG_COLOR_DARK_GREEN);
            }
        } else if ((row + col) % 2 == 0) {
            out.print(SET_BG_COLOR_WHITE);
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

    public static void resetColors(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }
}
