package org.fire_ball_mods.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandom;
import org.fire_ball_mods.Regen_chests;
import org.fire_ball_mods.util.NBTWeighted;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Getter
public class LinkedLoot extends Loot {
    public String blockId;
    public UUID uuid;
    public List<NBTWeighted> forcedNbts = new ArrayList<>();

    public LinkedLoot(UUID uuid, String blockId) {
        this.uuid = uuid;
        this.blockId = blockId;
    }

    @Override
    public NBTWeighted getNumberLoot(int numberLoot) {
        List<NBTWeighted> allNbts = getAllNbts();
        if(numberLoot < 0 || numberLoot > allNbts.size()) {
            return null;
        }
        return allNbts.get(numberLoot);
    }

    public void addForcedNbt(NBTWeighted nbt) {
        forcedNbts.add(nbt);
    }

    @Override
    public void removeNbt(NBTWeighted nbt) {
        if(!forcedNbts.remove(nbt)) {
            super.removeNbt(nbt);
        }
    }

    @Override
    public NBTTagCompound getLoot() {
        List<NBTWeighted> resultNbts = new ArrayList<>(forcedNbts);
        List<Integer> noUseNabors = Regen_chests.INSTANCE.linkedLootDataBase.getNumbersNoUseNaborsAndCheckClear(uuid);
        for(int i = 0; i < nbts.size(); i++) {
            if(!noUseNabors.contains(i)) {
                resultNbts.add(nbts.get(i));
            }
        }
        if(resultNbts.size() <= 0) {
            return null;
        }
        NBTWeighted nbtWeighted = WeightedRandom.getRandomItem(new Random(), resultNbts);
        if(nbts.contains(nbtWeighted)) {
            int number = nbts.indexOf(nbtWeighted);
            Regen_chests.INSTANCE.linkedLootDataBase.addNabor(uuid, number);
        }
        try {
            return JsonToNBT.getTagFromJson(nbtWeighted.getNbt());
        }
        catch (Exception e) {
            Regen_chests.LOG.error("Error load nbt: " + nbtWeighted.getNbt());
        }
        return null;
    }

    public boolean isForced(NBTWeighted nbt) {
        return forcedNbts.contains(nbt);
    }

    private List<NBTWeighted> getAllNbts() {
        List<NBTWeighted> result = new ArrayList<>(forcedNbts);
        result.addAll(nbts);
        return result;
    }
}
