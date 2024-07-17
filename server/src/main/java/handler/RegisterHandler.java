package handler;

import com.google.gson.Gson;
import service.RegisterService;

import java.io.IOException;

public class RegisterHandler {
    private final RegisterService registerService;
    private final Gson gson;

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
        this.gson = new Gson();
    }



    //EXCHANGE

}
