package org.fire_ball_mods.handlers;

import lombok.AllArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.fire_ball_mods.Regen_chests;
import org.fire_ball_mods.config.ChestsListDataBase;
import org.fire_ball_mods.model.LinkedLoot;
import org.fire_ball_mods.model.Loot;
import org.fire_ball_mods.model.RegenChestData;
import org.fire_ball_mods.util.MathUtils;
import org.fire_ball_mods.util.MyVector;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class RegenChestsHandler {
    private ChestsListDataBase dataBase;

    public void regenerateChest(TileEntity block, NBTTagCompound nbt) {
        block.deserializeNBT(nbt);
        block.markDirty();
        dataBase.updateTimeGetLoot(new MyVector(block.getPos(), block.getWorld().getWorldInfo().getWorldName()));
    }

    public void regenerateChest(TileEntity block) {
        BlockPos blockPos = block.getPos();
        String worldName = block.getWorld().getWorldInfo().getWorldName();
        MyVector pos = new MyVector(blockPos, worldName);
        if(!dataBase.isExists(pos)) {
            return;
        }
        RegenChestData dataRegen = dataBase.getChest(pos);
        Loot loot = Regen_chests.INSTANCE.lootDataBase.getLoot(dataRegen.getIdLoot());
        NBTTagCompound nbt = loot.getLoot();
        if(nbt == null) {
            return;
        }
        if(loot instanceof LinkedLoot) {
            nbt = prepareNbt(nbt, block);
        }
        regenerateChest(block,nbt);
    }

    public boolean isRegen(MyVector pos, long period) {
        Instant time = Instant.now();

        if(!dataBase.isExists(pos)) return false;
        Instant timeOpenedInstant = Instant.parse(dataBase.getChest(pos).lastTimeOpen);
        if(time.isBefore(timeOpenedInstant)) return false;
        return MathUtils.isTimeBeforeOnDelta(timeOpenedInstant, period);
    }

    public boolean isRegen(MyVector pos) {
        return isRegen(pos,Regen_chests.INSTANCE.config.INTERVAL_REGEN);
    }

    public long getLeftTime(MyVector pos) {
        if(!dataBase.isExists(pos)) {
            return -1;
        }
        Instant timeOpenedInstant = Instant.parse(dataBase.getChest(pos).lastTimeOpen);
        long lastTime = Duration.of(Regen_chests.INSTANCE.config.INTERVAL_REGEN, ChronoUnit.MINUTES).getSeconds()
                - (Instant.now().getEpochSecond()
                - timeOpenedInstant.getEpochSecond());
        return lastTime < 0 ? 0 : lastTime;
    }

    public long getLeftTimeLootNabor(LinkedLoot loot, int number) {
        if(!Regen_chests.INSTANCE.linkedLootDataBase.isExistsNaborLoot(loot.uuid, number)) {
            return -1;
        }
        String time = Regen_chests.INSTANCE.linkedLootDataBase.lootUseTime.get(loot.uuid).get(number);

        Instant timeUseInstant = Instant.parse(time);
        long lastTime = Duration.of(Regen_chests.INSTANCE.config.INTERVAL_REGEN, ChronoUnit.MINUTES).getSeconds()
                - (Instant.now().getEpochSecond()
                - timeUseInstant.getEpochSecond());
        return lastTime < 0 ? 0 : lastTime;
    }

    public List<MyVector> getAllChestsWithIdLoot(UUID idLoot) {
        return dataBase.getData().entrySet()
                .stream().filter(chest -> chest.getValue().getIdLoot().equals(idLoot))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public NBTTagCompound prepareNbt(NBTTagCompound nbt, TileEntity srcBlock) {
        String id = srcBlock.serializeNBT().getString("id");
        List<String> excludeNbt = Regen_chests.INSTANCE.excludeNBTConfig.getExcludesNBT(id);
        NBTTagCompound srcNbt = srcBlock.serializeNBT();
        excludeNbt.forEach(tagNbt -> {
            nbt.setTag(tagNbt, srcNbt.getTag(tagNbt));
        });
        return nbt;
    }
}
