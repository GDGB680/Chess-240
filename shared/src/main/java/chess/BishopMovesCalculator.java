package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = position.getRow();
        int currentCol = position.getColumn();
        ChessPiece bishop = board.getPiece(position);

        if (bishop == null) {
            return moves;
        }

        int[][] directions = {
                {-1, -1}, {-1, 1},
                {1, -1}, {1, 1}
        };

        for (int[] direction : directions) {
            int newRow = currentRow;
            int newCol = currentCol;

            while (true) {
                newRow += direction[0];
                newCol += direction[1];

                if (!isValidPosition(newRow, newCol)) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    moves.add(new ChessMove(position, newPosition, null));
                } else {
                    if (pieceAtNewPosition.getTeamColor() != bishop.getTeamColor()) {
                        moves.add(new ChessMove(position, newPosition, null));
                    }
                    break; // Stop after capturing or encountering own piece
                }
            }
        }

        return moves;
    }
}