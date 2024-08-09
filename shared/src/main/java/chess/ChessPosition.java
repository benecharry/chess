package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int row;
    private int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public ChessPosition(String position) {
        if (position != null && position.length() == 2) {
            char file = Character.toLowerCase(position.charAt(0));
            char rank = position.charAt(1);

            if (file >= 'a' && file <= 'h' && rank >= '1' && rank <= '8') {
                this.col = file - 'a' + 1;
                this.row = rank - '1' + 1;
                return;
            }
        }

        this.col = 1;
        this.row = 1;
        System.out.println("Invalid position: " + position + ". Defaulting to A1.");
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        char columnChar = (char) ('a' + (col - 1));
        return "" + columnChar + row;
    }
}
