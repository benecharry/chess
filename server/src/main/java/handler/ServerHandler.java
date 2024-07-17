package handler;

import com.google.gson.Gson;

public class ServerHandler {
    protected final Gson gson;

    public ServerHandler(Gson gson) {
        this.gson = new Gson();
    }


}
