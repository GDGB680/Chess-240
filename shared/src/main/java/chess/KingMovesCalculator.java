package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = position.getRow();
        int currentCol = position.getColumn();

        // Define all possible move directions for the king
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0},  {1, 1}
        };

        for (int[] direction : directions) {
            int newRow = currentRow + direction[0];
            int newCol = currentCol + direction[1];

            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            addMoveIfValid(moves, board, position, newPosition);
        }

        // TODO: Implement castling logic here

        return moves;
    }
}