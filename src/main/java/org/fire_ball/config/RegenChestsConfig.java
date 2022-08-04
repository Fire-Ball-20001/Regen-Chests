package org.fire_ball.config;

import net.minecraft.util.math.BlockPos;
import org.fire_ball.util.MyVector;
import org.fire_ball.util.RegenChest;

import java.util.HashMap;

public class RegenChestsConfig extends BaseConfig {

    public HashMap<MyVector, RegenChest> chests = new HashMap<>();

    public RegenChestsConfig(String path, String name) {
        super(path+name);
    }

    public boolean isExists(BlockPos chest) {
        return chests.containsKey(new MyVector(chest));
    }

    public void addChest(MyVector pos,RegenChest chest) {
        chests.put(pos,chest);
    }

    @Override
    public void copy(BaseConfig object) {
        chests = ((RegenChestsConfig) object).chests;
    }
}
