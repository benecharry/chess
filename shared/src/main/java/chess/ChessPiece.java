package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;
    private MovesCalculator movesCalculator;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        movesCalculate();
    }

    public ChessPiece(ChessPiece copy){
        this.pieceColor = copy.pieceColor;
        this.type = copy.type;
        movesCalculate();
    }

    void movesCalculate(){
        switch (this.type){
            case KING:
                this.movesCalculator = new MovementKing();
                break;
            case KNIGHT:
                this.movesCalculator = new MovementKnight();
                break;
            case PAWN:
                this.movesCalculator = new MovementPawn();
                break;
            case BISHOP:
                this.movesCalculator = new MovementBishop();
                break;
            case ROOK:
                this.movesCalculator = new MovementRook();
                break;
            case QUEEN:
                this.movesCalculator = new MovementQueen();
                break;
        }
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(this.movesCalculator != null){
            return this.movesCalculator.calculateValidMoves(board, myPosition);
        }
        else return new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
