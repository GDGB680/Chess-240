package chess;

import java.util.Collection;
import java.util.Objects;

public class ChessPiece {

    private PieceType pieceType;
    private ChessGame.TeamColor pieceColor;
    private PieceMovesCalculator movesCalculator;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceType = type;
        this.pieceColor = pieceColor;
        this.movesCalculator = createMovesCalculator(type);
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() { return this.pieceColor; }
    public PieceType getPieceType() { return this.pieceType; }

    private PieceMovesCalculator createMovesCalculator(PieceType type) {
        switch (type) {
            case KING: return new KingMovesCalculator();
            case QUEEN: return new QueenMovesCalculator();
            case BISHOP: return new BishopMovesCalculator();
            case KNIGHT: return new KnightMovesCalculator();
            case ROOK: return new RookMovesCalculator();
            case PAWN: return new PawnMovesCalculator();
            default: throw new IllegalArgumentException("Unknown piece type");
        }
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return movesCalculator.calculateMoves(board, myPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() { return Objects.hash(pieceColor, pieceType); }
}
