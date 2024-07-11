package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MovementKnight implements MovesCalculator {
    @Override
    public Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        // New movements tested.
        int[][] directions = {{2,-1}, {-2,1}, {2,1}, {-2,-1}, {-1,2}, {1,-2}, {-1,-2}, {1,2}};
        for(int[] direction : directions){
            int nextRow = position.getRow() + direction[0];
            int nextCol = position.getColumn() + direction[1];

            if(nextRow >= 1 && nextCol >= 1 && nextRow <= 8 && nextCol <= 8){
                ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
                if(board.isOccupied(nextPosition)){
                    if(board.getPiece(position).getTeamColor() != board.getPiece(nextPosition).getTeamColor()){
                        validMoves.add(new ChessMove(position, nextPosition, null));
                        }
                }
                else{
                    validMoves.add(new ChessMove(position, nextPosition, null));
                }
            }
        }
        return validMoves;
    }
}
