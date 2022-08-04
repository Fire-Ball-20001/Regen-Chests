package org.fire_ball.gson;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import org.fire_ball.util.RegenChest;

import java.lang.reflect.Type;
import java.util.HashMap;

public class RegenChestDeserializer implements JsonDeserializer<RegenChest> {
    @Override
    public RegenChest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        if(obj.get("items")==null) {
            throw new JsonParseException("Error parse");
        }
        if(obj.get("countItems")==null) {
            throw new JsonParseException("Error parse");
        }
        if(obj.get("isExample")==null) {
            throw new JsonParseException("Error parse");
        }
        return new RegenChest(obj.get("isExample").getAsBoolean(),
                obj.get("countItems").getAsInt(),
                context.deserialize(obj.get("items"),
                        new TypeToken<HashMap<Integer,String>>(){}.getType()));
    }
}
