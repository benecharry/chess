package handler;

import com.google.gson.Gson;

public class SerializationHandler {
    private static final Gson gson = new Gson();
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <Type> Type fromJson(String json, Class<Type> classOfType) {
        return gson.fromJson(json, classOfType);
    }

}
