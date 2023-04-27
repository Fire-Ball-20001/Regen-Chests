package org.fire_ball_mods.config;

import org.fire_ball_mods.Regen_chests;
import org.fire_ball_mods.model.RegenChestData;
import org.fire_ball_mods.util.MyVector;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChestsListDataBase extends BaseConfig{
    private HashMap<MyVector, RegenChestData> chests = new HashMap<>();

    public ChestsListDataBase(String path, String name) {
        super(path + name);
    }

    public boolean isExists(MyVector chest) {
        return chests.containsKey(chest);
    }

    public void addChest(MyVector pos, UUID idLoot) {
        if(isExists(pos)) {
            chests.remove(pos);
        }
        RegenChestData data = new RegenChestData(Instant.MIN.toString(), idLoot);
        chests.put(pos, data);
        save();
    }

    public void addChest(MyVector pos, RegenChestData data) {
        if(isExists(pos)) {
            chests.remove(pos);
        }
        chests.put(pos, data);
        save();
    }

    public RegenChestData getChest(MyVector pos) {
        return chests.get(pos);
    }

    public void updateTimeGetLoot(MyVector pos) {
        if(!isExists(pos)) {
            return;
        }
        RegenChestData data = getChest(pos);
        data.lastTimeOpen = Instant.now().toString();
        addChest(pos, data);
    }

    public boolean removeChestAndZeroLoot(MyVector pos) {
        if(isExists(pos)) {
            UUID idLoot = getChest(pos).idLoot;
            chests.remove(pos);
            if(!checkIdLoot(idLoot)) {
                Regen_chests.INSTANCE.lootDataBase.removeLoot(idLoot);
            }
            save();
            return true;
        }
        return false;
    }

    public boolean checkIdLoot(UUID loot) {
        return chests.values().stream().anyMatch((RegenChestData data) -> data.idLoot.equals(loot));
    }

    public int clearExcessChests() {
        List<Map.Entry<MyVector, RegenChestData>> oldChests = new ArrayList<>(chests.entrySet());
        AtomicInteger countDeletes = new AtomicInteger();
        oldChests.forEach(chest -> {
            if(!Regen_chests.INSTANCE.lootDataBase.isExists(chest.getValue().getIdLoot())) {
                chests.remove(chest.getKey());
                countDeletes.getAndIncrement();
            }
        });
        save();
        return countDeletes.get();
    }

    public HashMap<MyVector, RegenChestData> getData() {
        return chests;
    }

    @Override
    public void copy(BaseConfig object) {
        ChestsListDataBase dataBase = (ChestsListDataBase) object;
        chests = dataBase.chests;
    }

    public void printDebug(boolean isRegen, MyVector pos) {
        if(isRegen) {
            Regen_chests.LOG.info("Start regen: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
        }
        else {
            if(chests.containsKey(pos)) {
                Regen_chests.LOG.info("Block regen: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                Regen_chests.LOG.info(
                        "Left time: " + Regen_chests.INSTANCE.handler.getLeftTime(pos));
            } else {
                Regen_chests.LOG.info("No regenChest");
            }
        }
    }
}
