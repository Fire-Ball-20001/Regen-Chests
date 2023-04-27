package org.fire_ball_mods.model;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandom;
import org.fire_ball_mods.Regen_chests;
import org.fire_ball_mods.util.NBTWeighted;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Loot {
    public List<NBTWeighted> nbts = new ArrayList<>();

    public NBTTagCompound getLoot() {
        String nbt = WeightedRandom.getRandomItem(new Random(), nbts).getNbt();
        try {
            return JsonToNBT.getTagFromJson(nbt);
        }
        catch (Exception e) {
            Regen_chests.LOG.error("Error load nbt: " + nbt);
        }
        return null;
    }

    public NBTWeighted getNumberLoot(int numberLoot) {
        if(numberLoot < 0 || numberLoot > nbts.size()) {
            return null;
        }
        return nbts.get(numberLoot);
    }

    public void addNbt(NBTWeighted nbt) {
        nbts.add(nbt);
    }

    public void removeNbt(NBTWeighted nbt) {
        nbts.remove(nbt);
    }
}
