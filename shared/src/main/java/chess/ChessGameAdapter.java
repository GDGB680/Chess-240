package chess;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessGameAdapter implements JsonDeserializer<ChessGame> {
    public ChessGame deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject jsonObject = el.getAsJsonObject();
        String obj_type = jsonObject.get("type").getAsString();
        switch (obj_type) {
            case "KnightMovesCalculator":
                return ctx.deserialize(el, KnightMovesCalculator.class);
            case "KingMovesCalculator":
                return ctx.deserialize(el, KingMovesCalculator.class);
            case "BishopMovesCalculator":
                return ctx.deserialize(el, BishopMovesCalculator.class);
            case "PawnMovesCalculator":
                return ctx.deserialize(el, PawnMovesCalculator.class);
            case "QueenMovesCalculator":
                return ctx.deserialize(el, QueenMovesCalculator.class);
            case "RookMovesCalculator":
                return ctx.deserialize(el, RookMovesCalculator.class);

            default:
                throw new JsonParseException("Unknown type: " + type);
        }
    }
}