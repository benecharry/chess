package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public static void singleSquareMove(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition position,
                                        int[][] directions){
        //For each loop as suggested by the professor.
        for(int[] direction : directions){
            int nextRow = position.getRow() + direction[0];
            int nextCol = position.getColumn() + direction[1];
            if(ChessMove.isWithinBounds(nextRow, nextCol, true)){
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
    }

    public static void multipleSquareMove(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition position,
                                          int[][] directions){
        for(int[] direction:directions){
            int nextRow = position.getRow();
            int nextCol = position.getColumn();
            while(true){
                nextRow += direction[0];
                nextCol += direction[1];
                if(ChessMove.isWithinBounds(nextRow, nextCol, false)){
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
    }

    public static boolean isWithinBounds(int nextRow, int nextCol, boolean singleMove){
        if (singleMove) {
            return nextRow >= 1 && nextCol >= 1 && nextRow <= 8 && nextCol <= 8;
        } else {
            return nextRow < 1 || nextCol < 1 || nextRow > 8 || nextCol > 8;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) &&
                Objects.equals(endPosition, chessMove.endPosition) &&
                promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}

