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
            return new ArrayList<>();
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
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        if (movingPiece == null || movingPiece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("No piece at start position or wrong color");
        }
        if (!isValidMove(move)) {
            throw new InvalidMoveException("Invalid move");
        }
        // Perform the move
        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), movingPiece);
        board.addPiece(move.getStartPosition(), null);
        // Handle pawn promotion
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(currentTurn, move.getPromotionPiece()));
        }
        // Check if the move leaves the current player in check
        if (isInCheck(currentTurn)) {
            // Undo the move
            board.addPiece(move.getStartPosition(), movingPiece);
            board.addPiece(move.getEndPosition(), capturedPiece);
            throw new InvalidMoveException("Move leaves king in check");
        }
        // Switch turns
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
        if (piece == null) { return false; }
        // Check if the move is in the list of possible moves for the piece
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, move.getStartPosition());
        if (!possibleMoves.contains(move)) { return false; }
        // Temporarily make the move
        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
        // Check if the move leaves the piece's team in check
        boolean valid = !isInCheck(piece.getTeamColor());
        // Undo the move
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
        TeamColor attackingTeam = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition startPosition = new ChessPosition(row, col);
//                ChessPiece piece = board.getPiece(startPosition);
                evilTeam(board.getPiece(startPosition), new ChessPosition(row, col),(teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE, position);

            }
        }
        return false;
    }

    private boolean hasLegalMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
//                ChessPiece piece = board.getPiece(position);
                goodTeam(board.getPiece(position), teamColor, position);
            }
        }
        return false;
    }

    private boolean goodTeam(ChessPiece piece, TeamColor teamColor, ChessPosition position) {
        if (piece != null && piece.getTeamColor() == teamColor) {
            Collection<ChessMove> moves = piece.pieceMoves(board, position);
            for (ChessMove move : moves) {
                if (isValidMove(move)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean evilTeam(ChessPiece piece, ChessPosition startPosition,
                             TeamColor attackingTeam, ChessPosition position) {
        if (piece != null && piece.getTeamColor() == attackingTeam) {
            Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(position)) {
                    return true;
                }
            }
        }
        return false;
    }




}
