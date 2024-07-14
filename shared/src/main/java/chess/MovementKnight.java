package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MovementKnight implements MovesCalculator {
    @Override
    public Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {{2,-1}, {-2,1}, {2,1}, {-2,-1}, {-1,2}, {1,-2}, {-1,-2}, {1,2}};
        ChessMove.singleSquareMove(validMoves, board, position, directions);
        return validMoves;
    }
}
