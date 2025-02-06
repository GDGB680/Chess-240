package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGame {

    private TeamColor currentTurn;
    private ChessBoard board;

    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        this.currentTurn = TeamColor.WHITE;
    }

    public TeamColor getTeamTurn() { return currentTurn; }
    public void setTeamTurn(TeamColor team) { this.currentTurn = team; }

    public enum TeamColor {
        WHITE,
        BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : possibleMoves) {
            if (isValidMove(move)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!isValidMove(move)) {
            throw new InvalidMoveException("Invalid move");
        }
        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        board.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(currentTurn, move.getPromotionPiece()));
        }
        currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        return isPositionUnderAttack(kingPosition, teamColor);
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) { return false; }
        return !hasLegalMoves(teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) { return false; }
        return !hasLegalMoves(teamColor);
    }

    public void setBoard(ChessBoard board) { this.board = board; }
    public ChessBoard getBoard() { return this.board; }




    private boolean isValidMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != currentTurn) { return false; }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, move.getStartPosition());
        if (!possibleMoves.contains(move)) { return false; }
        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
        boolean valid = !isInCheck(currentTurn);
        board.addPiece(move.getStartPosition(), piece);
        board.addPiece(move.getEndPosition(), capturedPiece);
        return valid;
    }


    private ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
    }

    private boolean isPositionUnderAttack(ChessPosition position, TeamColor teamColor) {
        TeamColor attackingTeam;
        if (teamColor == TeamColor.WHITE) {
            attackingTeam = TeamColor.BLACK;
        } else {
            attackingTeam = TeamColor.WHITE;
        }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition startPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == attackingTeam) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(position)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hasLegalMoves(TeamColor teamColor) {
        // Implement logic to check if the team has any legal moves
        return false; // Placeholder
    }
}
