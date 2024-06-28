package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Rook implements CaculateMovesCalculator{
    @Override
    public Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int[][] directions = {{0,1},{0,-1},{1,0},{-1,0}};
        for(int[] direction : directions){
            int nextRow = position.getRow();
            int nextCol = position.getColumn();
            while(true){
                nextRow += direction[0];
                nextCol += direction[1];
                if(nextRow < 1 || nextCol < 1 || nextRow > 8 || nextCol > 8){
                    break;
                }
                ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
                if(board.isOccupied(nextPosition)){
                    if(board.getPiece(position).getTeamColor() != board.getPiece(nextPosition).getTeamColor()){
                        validMoves.add(new ChessMove(position, nextPosition, null));
                    }
                    break;
                }
                else{
                    validMoves.add(new ChessMove(position, nextPosition, null));
                }
            }
        }
        return validMoves;
    }
}
