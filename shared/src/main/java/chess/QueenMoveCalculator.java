package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoveCalculator implements MovesCalculator {
    @Override
    public Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {{0,1},{0,-1},{1,0},{-1,0},{1,1}, {-1,1}, {1,-1}, {-1,-1}};
        ChessMove.addValidMultipleSquareMoves(validMoves, board, position, directions);
        return validMoves;
    }
}
