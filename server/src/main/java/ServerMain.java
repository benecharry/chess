import server.Server;
import server.websocket.WebSocketHandler;
import spark.Spark;

public class ServerMain {
    public static void main(String[] args){
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Server: " + piece)
        Server server = new Server();
        server.run(8080);

        //Spark.webSocket("/ws", WebSocketHandler.class);
        //Spark.init();
    }
}