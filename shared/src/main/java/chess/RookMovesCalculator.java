package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece rook = board.getPiece(position);

        if (rook == null) {
            return moves;
        }
        this.teamColor = rook.getTeamColor();
        moves.addAll(calculateStraightMoves(board, position));
        return moves;
    }
}

