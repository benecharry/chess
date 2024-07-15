package chess;


import java.util.ArrayList;
import java.util.Collection;


public class MovementPawn implements MovesCalculator {
    @Override
    public Collection<ChessMove> calculateValidMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int forward = getPawnDirection(board, position);

        int[][] directions = {{forward, 0}, {forward, 1}, {forward, -1}};
        for(int[] direction : directions){
            int nextRow = position.getRow() + direction[0];
            int nextCol = position.getColumn() + direction[1];
            if(ChessMove.isWithinBounds(nextRow, nextCol, true)){
                ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
                if(direction[1] == 0){
                    if(!board.isOccupied(nextPosition)) {
                        if (isInPromotionPosition(forward, nextRow)) {
                            getPromotionMove(validMoves, position, nextPosition);
                        } else {
                            validMoves.add(new ChessMove(position, nextPosition, null));
                        }
                        getInitialMove(validMoves, forward, nextRow, nextCol, board, position);
                    }
                }
                else {
                    captureMove(validMoves, nextPosition, forward, board, position, nextRow);
                }
            }
        }
        return validMoves;
    }

    public int getPawnDirection(ChessBoard board, ChessPosition position){
        if(board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK){
            return -1;
        }
        else if(board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE){
            return 1;
        }
        return 0;
    }

    public boolean isInPromotionPosition(int forward, int nextRow){
        return forward == 1 && nextRow == 8 || forward == -1 && nextRow == 1;
    }

    public void getInitialMove(Collection<ChessMove> validMoves, int forward, int nextRow, int nextCol, ChessBoard board,
                           ChessPosition position){
            if (forward == 1 && position.getRow() == 2 || forward == -1 && position.getRow() == 7) {
                ChessPosition doubleMove = new ChessPosition(nextRow + forward, nextCol);
                if (!board.isOccupied(doubleMove)) {
                    validMoves.add(new ChessMove(position, doubleMove, null));
                }
        }
    }

    public void captureMove(Collection<ChessMove> validMoves, ChessPosition nextPosition, int forward, ChessBoard board,
                                     ChessPosition position, int nextRow){
        if(board.isOccupied(nextPosition) && board.getPiece(position).getTeamColor() != board.getPiece(nextPosition).getTeamColor()){
            if(isInPromotionPosition(forward, nextRow)){
                getPromotionMove(validMoves, position, nextPosition);
            }
            else{
                validMoves.add(new ChessMove(position, nextPosition, null));
            }
        }
    }


    public void getPromotionMove(Collection<ChessMove> validMoves, ChessPosition position, ChessPosition nextPosition){
        validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.ROOK));
        validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.BISHOP));
        validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.QUEEN));
        validMoves.add(new ChessMove(position, nextPosition, ChessPiece.PieceType.KNIGHT));
    }
}