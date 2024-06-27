package chess;

import java.util.Collection;

public interface CaculateMovesCalculator {
    Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position);
}
