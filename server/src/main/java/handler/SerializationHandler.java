package handler;

import chess.MovesCalculator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import chess.*;

import com.google.gson.*;
import java.lang.reflect.Type;

public class SerializationHandler {
    private static final Gson GSON = createSerializer();
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }
    //Similar to the one on JSON and Serialization.
    public static Gson createSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(ChessPiece.class,
                (JsonDeserializer<ChessPiece>) (el, type, ctx) -> {
            JsonObject jsonObject = el.getAsJsonObject();

            ChessGame.TeamColor teamColor = ctx.deserialize(jsonObject.get("pieceColor"), ChessGame.TeamColor.class);
            ChessPiece.PieceType pieceType = ChessPiece.PieceType.valueOf(jsonObject.get("type").getAsString());

            return new ChessPiece(teamColor, pieceType);
        });

        gsonBuilder.registerTypeAdapter(MovesCalculator.class,
                (JsonDeserializer<MovesCalculator>) (el, type, ctx) -> {
            ChessPiece.PieceType pieceType = ChessPiece.PieceType.valueOf(el.getAsJsonObject().get("type").getAsString());
            return switch (pieceType) {
                case PAWN -> new PawnMoveCalculator();
                case ROOK -> new RookMoveCalculator();
                case KNIGHT -> new KnightMoveCalculator();
                case BISHOP -> new BishopMoveCalculator();
                case QUEEN -> new QueenMoveCalculator();
                case KING -> new KingMoveCalculator();
            };
        });

        return gsonBuilder.create();
    }
}