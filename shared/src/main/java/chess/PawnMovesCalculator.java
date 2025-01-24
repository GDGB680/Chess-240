package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int currentRow = position.getRow();
        int currentCol = position.getColumn();
        ChessPiece pawn = board.getPiece(position);

        if (pawn == null) {
            return moves;
        }

        int direction = (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (pawn.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;

        // Move forward one square
        addPawnMove(board, position, direction, 0, moves, false);

        // Move forward two squares if it's the pawn's first move
        if (position.getRow() == startRow) {
            addPawnMove(board, position, direction * 2, 0, moves, false);
        }

        // Capture diagonally
        addPawnMove(board, position, direction, 1, moves, true);
        addPawnMove(board, position, direction, -1, moves, true);

        return moves;
    }

    private void addPawnMove(ChessBoard board, ChessPosition start, int rowOffset, int colOffset,
                             Collection<ChessMove> moves, boolean isCapture) {
        int newRow = start.getRow() + rowOffset;
        int newCol = start.getColumn() + colOffset;

        if (!isValidPosition(newRow, newCol)) {
            return;
        }

        ChessPosition end = new ChessPosition(newRow, newCol);
        ChessPiece pieceAtEnd = board.getPiece(end);

        boolean isValidMove = (isCapture && pieceAtEnd != null && pieceAtEnd.getTeamColor() != board.getPiece(start).getTeamColor()) ||
                (!isCapture && pieceAtEnd == null);

        if (isValidMove) {
            if (newRow == 1 || newRow == 8) {
                // Pawn promotion
                moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
                moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
            } else {
                moves.add(new ChessMove(start, end, null));
            }
        }
    }
}