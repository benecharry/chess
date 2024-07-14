package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MovementKing implements MovesCalculator {
    @Override
    public Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {{1,1}, {1,-1}, {-1,-1}, {-1,1}, {1,0}, {-1,0}, {0,1}, {0,-1}};
        ChessMove.singleSquareMove(validMoves, board, position, directions);
        return validMoves;
    }
}
