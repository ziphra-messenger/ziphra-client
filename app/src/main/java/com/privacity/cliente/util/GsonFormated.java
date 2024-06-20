package com.privacity.cliente.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class GsonFormated {

    public static Gson get(){
        return new GsonBuilder()
                //.setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
//                .registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter())
                .create();
    }

    // Using Android's base64 libraries. This can be replaced with any base64 library.
//    private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
//        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//            return new Gson().fromJson(json.toString(), byte[].class);
//        }
//
//        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
//            return new JsonPrimitive(src.toString());
//        }
//    }
}
