import dataaccess.*;
import manager.ServiceManager;
import server.Server;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Server: " + piece)
        UserDataDataAccess userDataDataAccess;
        AuthDataDataAccess authDataDataAccess;
        GameDataDataAccess gameDataDataAccess;


        Server server = new Server(serviceManager);
        server.run(8080);
    }
}