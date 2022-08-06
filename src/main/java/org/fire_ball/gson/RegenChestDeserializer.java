package org.fire_ball.gson;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import org.fire_ball.util.NBTWeighted;
import org.fire_ball.util.RegenChest;

import java.lang.reflect.Type;
import java.util.List;

public class RegenChestDeserializer implements JsonDeserializer<RegenChest> {
    @Override
    public RegenChest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        if(obj.get("nbt`s")==null) {
            throw new JsonParseException("Error parse");
        }
        return new RegenChest(
                context.deserialize(obj.get("nbt`s"),
                        new TypeToken<List<NBTWeighted>>(){}.getType()));
    }
}
