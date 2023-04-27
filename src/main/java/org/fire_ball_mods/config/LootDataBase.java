package org.fire_ball_mods.config;


import org.fire_ball_mods.Regen_chests;
import org.fire_ball_mods.model.LinkedLoot;
import org.fire_ball_mods.model.Loot;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LootDataBase extends BaseConfig {

    private HashMap<UUID, Loot> loots = new HashMap<>();
    private HashMap<UUID, LinkedLoot> linkedLoots = new HashMap<>();

    public LootDataBase(String path, String name) {
        super(path + name);
    }

    public boolean isExists(UUID id) {
        return loots.containsKey(id) || linkedLoots.containsKey(id);
    }

    public void addLoot(Loot loot) {
        addLoot(UUID.randomUUID(), loot);
    }
    public void addLoot(UUID id, Loot loot) {
        if(isExists(id)) {
            return;
        }
        if(loot instanceof LinkedLoot) {
            linkedLoots.put(id, (LinkedLoot) loot);
        } else {
            loots.put(id, loot);
        }
        save();
    }

    public void setLoot(UUID id, Loot loot) {
        if(!isExists(id)) {
            return;
        }
        removeLoot(id);
        addLoot(id, loot);
    }

    public Loot getLoot(UUID id) {
        if(linkedLoots.containsKey(id)) {
            return linkedLoots.get(id);
        }
        return loots.get(id);
    }

    public boolean removeLoot(UUID id) {
        if(isExists(id)) {
            if(linkedLoots.containsKey(id)) {
                linkedLoots.remove(id);
            } else {
                loots.remove(id);
            }
            save();
            return true;
        }
        return false;
    }

    public HashMap<UUID, Loot> getData() {
        HashMap<UUID, Loot> result = (HashMap<UUID, Loot>) loots.clone();
        result.putAll(linkedLoots);
        return result;
    }

    public int clearExcessLoots() {
        List<UUID> oldLoots = new ArrayList<>(loots.keySet());
        oldLoots.addAll(linkedLoots.keySet());
        AtomicInteger countDeletes = new AtomicInteger();
        oldLoots.forEach(loot -> {
            if(!Regen_chests.INSTANCE.chestsDataBase.checkIdLoot(loot)) {
                loots.remove(loot);
                countDeletes.getAndIncrement();
            }
        });
        save();
        return countDeletes.get();
    }

    public List<UUID> getAllUUID() {
        List<UUID> result = new ArrayList<>(loots.keySet());
        result.addAll(getLinkedUUID());
        return result;
    }

    public List<UUID> getLinkedUUID() {
        return new ArrayList<>(linkedLoots.keySet());
    }

    @Override
    public void copy(BaseConfig object) {
        LootDataBase config = (LootDataBase) object;
        loots = config.loots;
        linkedLoots = config.linkedLoots;
    }
}
