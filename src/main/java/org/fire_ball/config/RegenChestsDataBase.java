package org.fire_ball.config;

import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import org.fire_ball.Regen_chests;
import org.fire_ball.util.MyVector;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class RegenChestsDataBase extends BaseConfig {

    private HashMap<MyVector, String> chestsOpened = new HashMap<>();

    public RegenChestsDataBase(String path, String name) {
        super(path+name);
    }

    public boolean isRegen(BlockPos chest, Instant time, long period) {
        if(!chestsOpened.containsKey(new MyVector(chest))) return Regen_chests.INSTANCE.regenChestsConfig.isExists(chest);
        Instant timeOpenedInstant = Instant.parse(chestsOpened.get(new MyVector(chest)));
        if(time.isBefore(timeOpenedInstant)) return false;
        return time.getEpochSecond()-timeOpenedInstant.getEpochSecond() >= Duration.of(period, ChronoUnit.MINUTES).getSeconds();
    }

    public void addChest(BlockPos chest) {
        chestsOpened.put(new MyVector(chest),Instant.now().toString());
    }

    public void regenerateChest(ContainerChest chest) {
        if(chest.getLowerChestInventory() instanceof TileEntityChest) {
            BlockPos pos = ((TileEntityChest) chest.getLowerChestInventory()).getPos();
            if(isRegen(pos,Instant.now(),Regen_chests.INSTANCE.config.INTERVAL_REGEN)) {
                if(Regen_chests.INSTANCE.config.IS_DEBUG) {
                    Regen_chests.LOG.info("Start regen: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                }
                TileEntityChest tileEntity = (TileEntityChest) chest.getLowerChestInventory();
                tileEntity.clear();
                for(Map.Entry<Integer, ItemStack> item : Regen_chests.INSTANCE.regenChestsConfig.chests.get(new MyVector(pos)).getItems()) {
                    tileEntity.setInventorySlotContents(item.getKey(),item.getValue());
                }
                tileEntity.markDirty();
                addChest(tileEntity.getPos());
            }
            else {
                if(Regen_chests.INSTANCE.config.IS_DEBUG) {
                    if(chestsOpened.containsKey(new MyVector(pos))) {
                        Regen_chests.LOG.info("Block regen: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                        Instant timeOpenedInstant = Instant.parse(chestsOpened.get(new MyVector(pos)));
                        Regen_chests.LOG.info(
                                "Left time: " +
                                        (Duration.of(Regen_chests.INSTANCE.config.INTERVAL_REGEN, ChronoUnit.MINUTES).getSeconds()
                                                - (Instant.now().getEpochSecond()
                                                - timeOpenedInstant.getEpochSecond())));
                    } else {
                        Regen_chests.LOG.info("No regenChest");
                    }
                }

            }
        }
    }

    @Override
    public void copy(BaseConfig object) {
        this.chestsOpened = ((RegenChestsDataBase)object).chestsOpened;
    }
}
