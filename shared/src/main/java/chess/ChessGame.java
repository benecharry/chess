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
        this.board.resetBoard();

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

    public void changeTeamTurn(){
        this.teamTurn = getColorOfOpponent(this.teamTurn);
        this.newGame = false;
    }

    private TeamColor getColorOfOpponent(TeamColor teamColor) {
        TeamColor colorOfOpponent = null;
        if(teamColor == TeamColor.WHITE){
            colorOfOpponent = TeamColor.BLACK;
        }
        else if(teamColor == TeamColor.BLACK){
            colorOfOpponent = TeamColor.WHITE;
        }
        return colorOfOpponent;
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
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece == null) {
            throw new InvalidMoveException("No piece at given position " + startPosition + ".");
        }
        if(piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("It's not your turn.");
        }
        //I should use validMoves instead.
        Collection<ChessMove> validMoves = validMoves(startPosition);
        boolean isValidMove = false;
        for(ChessMove validMove : validMoves){
            if(validMove.getEndPosition().equals(endPosition)){
                isValidMove = true;
                if(piece.getPieceType() == ChessPiece.PieceType.PAWN && validMove.getPromotionPiece() != null){
                    piece = new ChessPiece(piece.getTeamColor(), validMove.getPromotionPiece());
                }
                break;
            }
        }
        if (!isValidMove) {
            throw new InvalidMoveException("Invalid move from " + startPosition + " to " + endPosition + ".");
        }
        //For moves that lead to check.
        ChessBoard temporaryBoard = new ChessBoard(board);
        temporaryBoard.addPiece(endPosition, piece);
        temporaryBoard.addPiece(startPosition, null);
        ChessBoard originalBoard = board;

        if(isInCheck(piece.getTeamColor())){
            board = originalBoard;
            throw new InvalidMoveException("Move leads to king being in check.");
        }

        board = originalBoard;

        try{
            board.addPiece(endPosition, piece);
            board.addPiece(startPosition, null);
            this.newGame = false;
            changeTeamTurn();
        } catch (Exception ex){
            throw new InvalidMoveException("Error during move", ex);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor colorOfOpponent = getColorOfOpponent(teamColor);
        ChessPosition kingPosition = board.findTheKing(teamColor);

        if (kingPosition == null) {
            throw new RuntimeException("King not found for " + teamColor);
        }

        Collection<ChessPosition> positions = board.allPosition();
        for (ChessPosition position : positions) {
            if (board.isOccupied(position)) {
                ChessPiece piece = board.getPiece(position);
                if(piece.getTeamColor() == colorOfOpponent) {
                    Collection<ChessMove> moves = validMoves(position);
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
        TeamColor colorOfOpponent = getColorOfOpponent(teamColor);
        ChessPosition kingPosition = board.findTheKing(teamColor);

        if (kingPosition == null) {
            throw new RuntimeException("King not found for " + teamColor);
        }

        if(!isInCheck(teamColor)){
            return false;
        }

        Collection<ChessPosition> startPositions = new ArrayList<>();
        Collection<ChessPosition> positions = board.allPosition();
        for(ChessPosition position : positions){
            if(board.isOccupied(position) && board.getPiece(position).getTeamColor() == teamColor){
                startPositions.add(position);
            }
        }
        for(ChessPosition startPosition : startPositions){
            Collection<ChessMove> validMoves = validMoves(startPosition);
            for(ChessMove validMove : validMoves){
                ChessBoard temporaryBoard = new ChessBoard(board);
                ChessPiece movePiece = temporaryBoard.getPiece(startPosition);
                temporaryBoard.addPiece(validMove.getEndPosition(), movePiece);
                temporaryBoard.addPiece(startPosition, null);

                ChessBoard originalBoard = this.board;
                board = temporaryBoard;

                if(!isInCheck(teamColor)){
                    this.board = originalBoard;
                    return false;
                }
                board = originalBoard;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(this.newGame){
            return false;
        }

        if(isInCheck(teamColor)){
            return false;
        }

        Collection<ChessPosition> positions = board.allPosition();
        for (ChessPosition startPosition : positions) {
            if (board.isOccupied(startPosition) && board.getPiece(startPosition).getTeamColor() == teamColor) {
                ChessPiece piece = board.getPiece(startPosition);
                Collection<ChessMove> validMoves = validMoves(startPosition);
                for(ChessMove move : validMoves){
                    ChessBoard temporaryBoard = new ChessBoard(board);
                    ChessPosition endPosition = move.getEndPosition();

                    temporaryBoard.addPiece(endPosition, piece);
                    temporaryBoard.addPiece(startPosition, null);

                    ChessBoard originalBoard = this.board;
                    board = temporaryBoard;

                    if(!isInCheck(teamColor)){
                        board = originalBoard;
                        return false;
                    }
                    board = originalBoard;
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = new ChessBoard(board);
        //Set this to false so pinned king woks!!!!!
        this.newGame = false;
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
