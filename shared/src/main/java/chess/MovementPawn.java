package chess;


import java.util.ArrayList;
import java.util.Collection;


public class MovementPawn implements MovesCalculator {
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
                            pawnPromotionPiece(validMoves, position, nextPosition);
                        } else {
                            validMoves.add(new ChessMove(position, nextPosition, null));
                        }
                        pawnDoubleMove(validMoves, forward, nextRow, nextCol, board, position);
                    }
                }
                else {
                    pawnCapturePromotion(validMoves, nextPosition, forward, board, position, nextRow);
                }
            }
        }
        return validMoves;
    }

    public void pawnDoubleMove(Collection<ChessMove> validMoves, int forward, int nextRow, int nextCol, ChessBoard board,
                           ChessPosition position){
            if (forward == 1 && position.getRow() == 2 || forward == -1 && position.getRow() == 7) {
                ChessPosition doubleMove = new ChessPosition(nextRow + forward, nextCol);
                if (!board.isOccupied(doubleMove)) {
                    validMoves.add(new ChessMove(position, doubleMove, null));
                }
        }
    }

    public void pawnCapturePromotion(Collection<ChessMove> validMoves, ChessPosition nextPosition, int forward, ChessBoard board,
                                     ChessPosition position, int nextRow){
        if(board.isOccupied(nextPosition) && board.getPiece(position).getTeamColor() != board.getPiece(nextPosition).getTeamColor()){
            if(forward == 1 && nextRow == 8 || forward == -1 && nextRow == 1){
                pawnPromotionPiece(validMoves, position, nextPosition);
            }
            else{
                validMoves.add(new ChessMove(position, nextPosition, null));
            }
        }
    }


    public void pawnPromotionPiece(Collection<ChessMove> validMoves, ChessPosition position, ChessPosition nextPosition){
        validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.BISHOP));
        validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.KNIGHT));
    }
}