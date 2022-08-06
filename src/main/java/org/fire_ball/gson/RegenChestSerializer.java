package org.fire_ball.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.fire_ball.util.RegenChest;

import java.lang.reflect.Type;

public class RegenChestSerializer implements JsonSerializer<RegenChest> {
    @Override
    public JsonElement serialize(RegenChest src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.add("nbt`s",context.serialize(src.nbts));
        return obj;
    }
}
