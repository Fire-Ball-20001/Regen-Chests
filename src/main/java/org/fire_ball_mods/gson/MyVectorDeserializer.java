package org.fire_ball_mods.gson;

import com.google.gson.*;
import org.fire_ball_mods.util.MyVector;

import java.lang.reflect.Type;

public class MyVectorDeserializer implements JsonDeserializer<MyVector> {

    @Override
    public MyVector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        if(obj.get("x") == null || obj.get("y") == null || obj.get("z") == null) {
            throw new JsonParseException("Error parse");
        }
        int x = obj.get("x").getAsInt();
        int y = obj.get("y").getAsInt();
        int z = obj.get("z").getAsInt();
        String world = "world";
        if(obj.get("world") != null) {
            world = obj.get("world").getAsString();
        }
        return new MyVector(x,y,z,world);
    }
}
