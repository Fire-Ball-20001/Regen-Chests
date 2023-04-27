package org.fire_ball_mods.gson;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import org.fire_ball_mods.util.NBTWeighted;
import org.fire_ball_mods.model.OldRegenChest;

import java.lang.reflect.Type;
import java.util.List;

public class RegenChestDeserializer implements JsonDeserializer<OldRegenChest> {
    @Override
    public OldRegenChest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        if(obj.get("nbt`s")==null) {
            throw new JsonParseException("Error parse");
        }
        return new OldRegenChest(
                context.deserialize(obj.get("nbt`s"),
                        new TypeToken<List<NBTWeighted>>(){}.getType()));
    }
}
