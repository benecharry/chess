package chess;

import java.util.ArrayList;
import java.util.Collection;

public class King implements CaculateMovesCalculator{
    @Override
    public Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {{1,1}, {1,-1}, {-1,-1}, {-1,1}, {1,0}, {-1,0}, {0,1}, {0,-1}};
//        For each loop as suggested by the professor.
        for(int[] direction : directions){
            int nextRow = position.getRow() + direction[0];
            int nextCol = position.getColumn() + direction[1];
//          Checking that we do not leave the valid space of the board.
            if(nextRow >= 1 && nextCol >=1 && nextRow <= 8 && nextCol <=8 ){
                ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
                if(board.isOccupied(nextPosition)){
                    if(board.getPiece(position).getTeamColor() != board.getPiece(nextPosition).getTeamColor()){
                        validMoves.add(new ChessMove(position, nextPosition, null));
                    }
                }
                else {validMoves.add(new ChessMove(position, nextPosition, null));
                }
            }
        }
        return validMoves;
    }
}
