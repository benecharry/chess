package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessGame.TeamColor teamColor;
    private ChessBoard board;
    private boolean newGame;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        //For later. White always starts.
        this.teamTurn = TeamColor.WHITE;
        this.newGame = true;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(!board.isOccupied(startPosition) || board.getPiece(startPosition) == null){
            return null;
        }
        // Save piece from location.
        ChessPiece piece = board.getPiece(startPosition);
        //piece.movesCalculate();
        return piece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }
    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        if(this.newGame == true){
            return false;
        }
        TeamColor colorOfOpponent = null;
        if(teamColor == TeamColor.WHITE){
            colorOfOpponent = TeamColor.BLACK;
        }
        else if(teamColor == TeamColor.BLACK){
            colorOfOpponent = TeamColor.WHITE;
        }

        ChessPosition kingPosition = board.findTheKing(teamColor);
        if (kingPosition == null) {
            throw new RuntimeException("Not implemented");
        }

        Collection<ChessPosition> positions = board.allPosition();
        for (ChessPosition position : positions) {
            if (board.isOccupied(position)) {
                ChessPiece piece = board.getPiece(position);
                if(piece.getTeamColor() == colorOfOpponent) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            //If same location (?) as kingPosition?
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(this.newGame == true){
            return false;
        }
        throw new RuntimeException("Not implemented");
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(this.newGame == true){
            return false;
        }
        throw new RuntimeException("Not implemented");
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && teamColor == chessGame.teamColor && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, teamColor, board);
    }
}
