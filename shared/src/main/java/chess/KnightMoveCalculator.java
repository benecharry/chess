package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator implements MovesCalculator {
    @Override
    public Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {{2,-1}, {-2,1}, {2,1}, {-2,-1}, {-1,2}, {1,-2}, {-1,-2}, {1,2}};
        ChessMove.addValidSingleSquareMoves(validMoves, board, position, directions);
        return validMoves;
    }
}
