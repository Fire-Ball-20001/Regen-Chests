package org.fire_ball_mods.config;

import com.google.common.collect.ImmutableMap;
import org.fire_ball_mods.Regen_chests;
import org.fire_ball_mods.util.MathUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LinkedLootDataBase extends BaseConfig{

    public HashMap<UUID, HashMap<Integer, String>> lootUseTime = new HashMap<>();

    public LinkedLootDataBase(String path, String name) {
        super(path + name);
    }

    public boolean isExistsLoot(UUID id) {
        return lootUseTime.containsKey(id);
    }

    public boolean isExistsNaborLoot(UUID id, int number) {
        if(isExistsLoot(id)) {
            return lootUseTime.get(id).containsKey(number);
        }
        return false;
    }

    public boolean isUse(UUID idLoot, int numberNabor) {
        if(isExistsLoot(idLoot)) {
            if(lootUseTime.get(idLoot).containsKey(numberNabor)) {
                Instant timeUse = Instant.parse(lootUseTime.get(idLoot).get(numberNabor));
                return MathUtils.isTimeBeforeOnDelta(timeUse, Regen_chests.INSTANCE.config.INTERVAL_REGEN);
            }
        }
        return true;
    }

    public List<Integer> getNumbersNoUseNaborsAndCheckClear(UUID idLoot) {
        clearActiveNabors();
        List<Integer> result = new ArrayList<>();
        if(lootUseTime.containsKey(idLoot)) {
            result.addAll(lootUseTime.get(idLoot).keySet());
        }
        return result;
    }

    public void addNabor(UUID id, int numberNabor) {
        if(lootUseTime.containsKey(id)) {
            lootUseTime.get(id).put(numberNabor, Instant.now().toString());
        } else {
            HashMap<Integer, String> nabors = new HashMap<>();
            nabors.put(numberNabor, Instant.now().toString());
            lootUseTime.put(id, nabors);
        }
        save();
    }

    public void removeNabor(UUID id, int number) {
        if(isExistsNaborLoot(id, number)) {
            lootUseTime.get(id).remove(number);
            save();
        }
    }

    public void clearActiveNabors() {
        List<UUID> deletes = new ArrayList<>();
        lootUseTime.forEach((idLoot, loot) -> {
            if(loot.entrySet().stream().anyMatch(oneLoot -> isUse(idLoot, oneLoot.getKey()))) {
                deletes.add(idLoot);
            }
        });
        deletes.forEach(id -> lootUseTime.remove(id));
        save();
    }

    @Override
    public void copy(BaseConfig object) {
        LinkedLootDataBase linkedLootDataBase = (LinkedLootDataBase) object;
        lootUseTime = linkedLootDataBase.lootUseTime;
    }
}
