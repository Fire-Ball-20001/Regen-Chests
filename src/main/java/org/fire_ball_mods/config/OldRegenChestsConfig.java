package org.fire_ball_mods.config;

import org.fire_ball_mods.util.MyVector;
import org.fire_ball_mods.model.OldRegenChest;

import java.util.HashMap;

public class OldRegenChestsConfig extends BaseConfig {

    public HashMap<MyVector, OldRegenChest> chests = new HashMap<>();

    public OldRegenChestsConfig(String path, String name) {
        super(path+name);
    }

    public boolean isExists(MyVector chest) {
        return chests.containsKey(chest);
    }

    public void addChest(MyVector pos, OldRegenChest chest) {
        if(chest.nbts.size() < 1 && isExists(pos)) {
            removeChest(pos);
            return;
        }
        else if (chest.nbts.size() < 1) {
            return;
        }
        chests.put(pos,chest);
        save();
    }

    public OldRegenChest getChest(MyVector pos) {
        return chests.get(pos);
    }

    public void removeChest(MyVector pos) {
        chests.remove(pos);
        //Regen_chests.INSTANCE.oldRegenChestsDataBase.removeChest(pos);
        save();
    }
    @Override
    public void copy(BaseConfig object) {
        chests = ((OldRegenChestsConfig) object).chests;
    }
}
