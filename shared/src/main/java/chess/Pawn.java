package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Pawn implements CaculateMovesCalculator{

    @Override
    public Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int forward = 0;
        if(board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK){
            forward = -1;
        }
        else if(board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE){
            forward = 1;
        }

        int[][] directions = {{forward, 0}, {forward, 1}, {forward, -1}};
        for(int[] direction : directions){
            int nextRow = position.getRow() + direction[0];
            int nextCol = position.getColumn() + direction[1];
            if(nextRow >= 1 && nextCol >= 1 && nextRow <= 8 && nextCol <= 8){
                ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
                if(direction[1] == 0){
                    if(!board.isOccupied(nextPosition)) {
                        if (forward == 1 && nextRow == 8 || forward == -1 && nextRow == 1) {
                            promotionPiece(validMoves, position, nextPosition);
                        } else {
                            validMoves.add(new ChessMove(position, nextPosition, null));
                        }
                    }
                }
            }
        }
        return validMoves;

    }

    public void promotionPiece(Collection<ChessMove> validMoves, ChessPosition position, ChessPosition nextPosition){
        validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.BISHOP));
        validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.KNIGHT));
    }
}
