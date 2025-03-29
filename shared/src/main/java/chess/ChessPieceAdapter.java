package chess;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

public class ChessPieceAdapter implements JsonDeserializer<ChessPiece> {
    private final Map<String, Class<? extends PieceMovesCalculator>> calculatorTypeMap = Map.of(
            "knight", KnightMovesCalculator.class,
            "king", KingMovesCalculator.class,
            "bishop", BishopMovesCalculator.class,
            "pawn", PawnMovesCalculator.class,
            "rook", RookMovesCalculator.class,
            "queen", QueenMovesCalculator.class
    );

    public ChessPiece deserialize(JsonElement el, Type type, JsonDeserializationContext ctx)
            throws JsonParseException {
        JsonObject obj = el.getAsJsonObject();
        ChessPiece.PieceType pieceType = ChessPiece.PieceType.valueOf(obj.get("pieceType").getAsString());
        ChessGame.TeamColor pieceColor = ChessGame.TeamColor.valueOf(obj.get("pieceColor").getAsString());
        ChessPiece piece = new ChessPiece(pieceColor, pieceType);

        Class <? extends PieceMovesCalculator> calculatorClass = calculatorTypeMap.get(piece.getPieceType()
                .toString().toLowerCase());
        if (calculatorClass == null) {
            throw new JsonParseException("Piece type unknown: " + calculatorClass.toString());
        }
        piece.setMovesCalculator(pieceType);
        return piece;

        }

}