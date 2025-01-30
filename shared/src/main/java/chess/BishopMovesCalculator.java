package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece bishop = board.getPiece(position);

        if (bishop == null) {
            return moves;
        }
        this.teamColor = bishop.getTeamColor();


        moves.addAll(calculateDiagonalMoves(board, position));
        return moves;
    }
}
