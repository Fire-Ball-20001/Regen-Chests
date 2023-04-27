package org.fire_ball_mods.model;

import lombok.AllArgsConstructor;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandom;
import org.fire_ball_mods.Regen_chests;
import org.fire_ball_mods.util.NBTWeighted;

import java.util.List;
import java.util.Random;

@AllArgsConstructor
public class OldRegenChest {
    public List<NBTWeighted> nbts;

    public NBTTagCompound getNbt() {
        String nbt = WeightedRandom.getRandomItem(new Random(), nbts).getNbt();
        try {
            return JsonToNBT.getTagFromJson(nbt);
        }
        catch (Exception e) {
            Regen_chests.LOG.error("Error load nbt: " + nbt);
        }
        return null;
    }

    public NBTWeighted getNbtWeight(int naborPos) {
        if(naborPos<0 || naborPos>nbts.size()) {
            return null;
        }
        return nbts.get(naborPos);
    }

    public void addNbt(NBTWeighted nbt) {
        nbts.add(nbt);
    }

    public void removeNbt(NBTWeighted nbt) {
        nbts.remove(nbt);
    }
}
