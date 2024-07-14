package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MovementBishop implements MovesCalculator {
    @Override
    public Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {{1,1}, {-1,1}, {1,-1}, {-1,-1}};
        ChessMove.multipleSquareMove(validMoves, board, position, directions);
        return validMoves;
    }
}
