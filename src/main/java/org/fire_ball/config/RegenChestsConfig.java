package org.fire_ball.config;

import net.minecraft.util.math.BlockPos;
import org.fire_ball.Regen_chests;
import org.fire_ball.util.MyVector;
import org.fire_ball.util.RegenChest;

import java.util.HashMap;

public class RegenChestsConfig extends BaseConfig {

    public HashMap<MyVector, RegenChest> chests = new HashMap<>();

    public RegenChestsConfig(String path, String name) {
        super(path+name);
    }

    public boolean isExists(MyVector chest) {
        return chests.containsKey(chest);
    }

    public void addChest(MyVector pos,RegenChest chest) {
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

    public RegenChest getChest(MyVector pos) {
        return chests.get(pos);
    }

    public void removeChest(MyVector pos) {
        chests.remove(pos);
        Regen_chests.INSTANCE.regenChestsDataBase.removeChest(pos);
        save();
    }
    @Override
    public void copy(BaseConfig object) {
        chests = ((RegenChestsConfig) object).chests;
    }
}
