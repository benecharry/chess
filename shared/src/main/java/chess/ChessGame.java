package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    private boolean isANewGame;
    private boolean ignoreCheckForValidMoves;
    private boolean isGameOver;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        //For later. White always starts.
        this.teamTurn = TeamColor.WHITE;
        this.isANewGame = true;
        //this.board.resetBoard();
        //Flag for validMoves;
        this.ignoreCheckForValidMoves = false;

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

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public void switchTeamTurn(){
        this.teamTurn = getOpponentColor(this.teamTurn);
        this.isANewGame = false;
    }

    public TeamColor getOpponentColor(TeamColor teamColor) {
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
            return Collections.emptyList();
        }
        // Save piece from location.
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> validMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> actualValidMoves = new ArrayList<>();

        ChessPiece.PieceType[] promotionTypes = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KNIGHT
        };

        for(ChessMove move : validMoves){
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece newPosition = board.getPiece(endPosition);

            board.addPiece(startPosition, null);
            board.addPiece(endPosition, piece);

            if (ignoreCheckForValidMoves || !isInCheck(piece.getTeamColor())) {
                if(piece.getPieceType() == ChessPiece.PieceType.PAWN && (endPosition.getRow() == 1 ||
                        endPosition.getRow() == 8)){
                    for (ChessPiece.PieceType promotionType : promotionTypes) {
                        ChessMove promotionMove = new ChessMove(startPosition, endPosition, promotionType);
                        actualValidMoves.add(promotionMove);
                    }
                }
                else{
                    actualValidMoves.add(move);
                }
            }

            board.addPiece(startPosition, piece);
            board.addPiece(endPosition, newPosition);
        }
        return actualValidMoves;
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

        Collection<ChessMove> validMoves = validMoves(startPosition);
        boolean isValidMove = false;
        ChessPiece.PieceType promotionPiece = null;

        for(ChessMove validMove : validMoves){
            if (validMove.getEndPosition().equals(endPosition)) {
                isValidMove = true;
                promotionPiece = validMove.getPromotionPiece();
                break;
            }
        }

        if(!isValidMove){
            throw new InvalidMoveException("Invalid move of" + piece + ".");
        }

        board.addPiece(startPosition, null);

        if (promotionPiece != null) {
            board.addPiece(endPosition, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        } else {
            board.addPiece(endPosition, piece);
        }

        switchTeamTurn();

        TeamColor opponentColor = getOpponentColor(piece.getTeamColor());

        if (isInCheckmate(opponentColor)) {
            setGameOver(true);
            System.out.println(opponentColor.name().toLowerCase() + " is in checkmate. Game over.");
        } else if (isInStalemate(opponentColor)) {
            setGameOver(true);
            System.out.println(opponentColor.name().toLowerCase() + " is in stalemate. Game over.");
        } else if (isInCheck(opponentColor)) {
            System.out.println(opponentColor.name().toLowerCase() + " is in check.");
        }
    }

    // TO-DO

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        if(this.isANewGame == true){
            return false;
        }
        TeamColor colorOfOpponent = getOpponentColor(teamColor);
        ChessPosition kingPosition = board.findKing(teamColor);

        Collection<ChessPosition> positions = board.findPositions();
        for (ChessPosition position : positions) {
            if (!board.isOccupied(position)) {
                continue;
            }

            ChessPiece piece = board.getPiece(position);
            if (piece.getTeamColor() != colorOfOpponent) {
                continue;
            }

            ignoreCheckForValidMoves = true;
            Collection<ChessMove> validMoves = validMoves(position);
            ignoreCheckForValidMoves = false;

            for (ChessMove move : validMoves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
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
        if(this.isANewGame == true){
            return false;
        }
        if(!isInCheck(teamColor)){
            return false;
        }

        return canEscapeCheck(teamColor);
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(this.isANewGame == true){
            return false;
        }
        if(isInCheck(teamColor)){
            return false;
        }

        return canEscapeCheck(teamColor);
    }

    private boolean canEscapeCheck(TeamColor teamColor) {
        Collection<ChessPosition> positions = board.findPositions();
        for (ChessPosition startPosition : positions) {
            if (board.isOccupied(startPosition) && board.getPiece(startPosition).getTeamColor() == teamColor) {
                Collection<ChessMove> validMoves = validMoves(startPosition);
                for(ChessMove move : validMoves){
                    ChessPiece movingPiece = board.getPiece(move.getStartPosition());
                    ChessPiece targetPiece = board.getPiece(move.getEndPosition());

                    board.addPiece(move.getStartPosition(), null);
                    board.addPiece(move.getEndPosition(), movingPiece);

                    if(!isInCheck(teamColor)){
                        board.addPiece(move.getStartPosition(), movingPiece);
                        board.addPiece(move.getEndPosition(), targetPiece);
                        return false;
                    }

                    board.addPiece(move.getStartPosition(), movingPiece);
                    board.addPiece(move.getEndPosition(), targetPiece);

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
        this.isANewGame = false;
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
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && teamColor == chessGame.teamColor &&
                Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, teamColor, board);
    }
}
