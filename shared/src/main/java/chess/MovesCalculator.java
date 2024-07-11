package chess;

import java.util.Collection;

public interface MovesCalculator {
    Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position);
}
