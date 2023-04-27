package org.fire_ball_mods.config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.fire_ball_mods.Regen_chests;
import org.fire_ball_mods.gson.MyVectorDeserializer;
import org.fire_ball_mods.gson.RegenChestDeserializer;
import org.fire_ball_mods.gson.RegenChestSerializer;
import org.fire_ball_mods.util.MyVector;
import org.fire_ball_mods.model.OldRegenChest;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class BaseConfig {
    transient private final String path;


    public BaseConfig(String path) {
        this.path = path;
    }

    public void save()
    {
        String mainPath = Regen_chests.MAIN_FOLDER+path;
        try {
            Files.createDirectories(Paths.get(mainPath).getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Files.exists(Paths.get(mainPath))) {
            try {
                Files.delete(Paths.get(mainPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try(FileWriter fw = new FileWriter(mainPath))
        {
            fw.write(getGson().toJson(this));
        }
        catch (Exception e)
        {
            Regen_chests.LOG.error("Error save config "+path);
            Regen_chests.LOG.error(e.getMessage());
        }
    }

    public void load()
    {
        String mainPath = Regen_chests.MAIN_FOLDER+path;
        try(FileReader fr = new FileReader(mainPath))
        {
            BaseConfig config = getGson().fromJson(fr,this.getClass());
            this.copy(config);
        }
        catch (Exception e)
        {
            Regen_chests.LOG.error("Error load config "+path);
            Regen_chests.LOG.error(e.getMessage());
            save();
        }
    }

    public abstract void copy(BaseConfig object);

    private Gson getGson() {
        return new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(OldRegenChest.class, new RegenChestSerializer())
                .registerTypeAdapter(OldRegenChest.class, new RegenChestDeserializer())
                .registerTypeAdapter(MyVector.class, new MyVectorDeserializer())
                .enableComplexMapKeySerialization()
                .create();
    }

}
