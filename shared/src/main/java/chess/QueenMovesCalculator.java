package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece queen = board.getPiece(position);

        if (queen == null) {
            return moves;
        }
        this.teamColor = queen.getTeamColor();

        moves.addAll(calculateStraightMoves(board, position));
        moves.addAll(calculateDiagonalMoves(board, position));

        return moves;
    }
}
