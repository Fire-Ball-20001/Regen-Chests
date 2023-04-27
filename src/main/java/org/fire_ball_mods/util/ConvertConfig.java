package org.fire_ball_mods.util;

import org.fire_ball_mods.Regen_chests;
import org.fire_ball_mods.config.OldRegenChestsConfig;
import org.fire_ball_mods.config.OldRegenChestsDataBase;
import org.fire_ball_mods.model.Loot;
import org.fire_ball_mods.model.OldRegenChest;
import org.fire_ball_mods.model.RegenChestData;

import java.util.UUID;

public class ConvertConfig {
    public static void convert(String oldConfigVersion) {
        Regen_chests.LOG.info("Convert config version: " + oldConfigVersion);
        if(oldConfigVersion.equals("0.9.4")) {
            OldRegenChestsDataBase oldRegenChestsDataBase = new OldRegenChestsDataBase("/data/","regenChests.json");
            OldRegenChestsConfig oldRegenChestsConfig = new OldRegenChestsConfig("","regenChests.cfg");
            oldRegenChestsConfig.load();
            oldRegenChestsDataBase.load();
            Regen_chests.INSTANCE.chestsDataBase.load();
            Regen_chests.INSTANCE.lootDataBase.load();
            oldRegenChestsConfig.chests.forEach((MyVector pos, OldRegenChest oldData) -> {
                UUID idLoot = UUID.randomUUID();
                RegenChestData data = new RegenChestData(oldRegenChestsDataBase.getData().get(pos),idLoot);
                Loot loot = new Loot();
                oldData.nbts.forEach(loot::addNbt);
                Regen_chests.INSTANCE.lootDataBase.addLoot(idLoot, loot);
                Regen_chests.INSTANCE.chestsDataBase.addChest(pos, data);
            });

            Regen_chests.INSTANCE.chestsDataBase.save();
            Regen_chests.INSTANCE.lootDataBase.save();
        }
        Regen_chests.INSTANCE.config.VERSION_CONFIG = Regen_chests.VERSION_CONFIG;
        Regen_chests.INSTANCE.config.save();
    }
}
