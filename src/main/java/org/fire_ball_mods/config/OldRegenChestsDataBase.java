package org.fire_ball_mods.config;

import org.fire_ball_mods.Regen_chests;
import org.fire_ball_mods.util.MyVector;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class OldRegenChestsDataBase extends BaseConfig {

    private HashMap<MyVector, String> chestsOpened = new HashMap<>();

    public OldRegenChestsDataBase(String path, String name) {
        super(path+name);
    }

    public boolean isRegen(MyVector chest, Instant time, long period) {
        //if(!chestsOpened.containsKey(chest)) return Regen_chests.INSTANCE.oldRegenChestsConfig.isExists(chest);
        Instant timeOpenedInstant = Instant.parse(chestsOpened.get(chest));
        if(time.isBefore(timeOpenedInstant)) return false;
        return time.getEpochSecond()-timeOpenedInstant.getEpochSecond() >= Duration.of(period, ChronoUnit.MINUTES).getSeconds();
    }

    public long getLeftTime(MyVector pos) {
        if(!chestsOpened.containsKey(pos)) {
            return -1;
        }
        Instant timeOpenedInstant = Instant.parse(chestsOpened.get(pos));
        long lastTime = Duration.of(Regen_chests.INSTANCE.config.INTERVAL_REGEN, ChronoUnit.MINUTES).getSeconds()
                - (Instant.now().getEpochSecond()
                - timeOpenedInstant.getEpochSecond());
        return lastTime < 0 ? 0 : lastTime;
    }

    public boolean isRegen(MyVector pos) {
        return isRegen(pos,Instant.now(),Regen_chests.INSTANCE.config.INTERVAL_REGEN);
    }

    public void addChest(MyVector chest) {
        chestsOpened.put(chest,Instant.now().toString());
        save();
    }

    public void removeChest(MyVector pos) {
        chestsOpened.remove(pos);
        save();
    }

    public HashMap<MyVector,String> getData() {
        return chestsOpened;
    }

    @Override
    public void copy(BaseConfig object) {
        this.chestsOpened = ((OldRegenChestsDataBase)object).chestsOpened;
    }

    public void printDebug(boolean isRegen, MyVector pos) {
        if(isRegen) {
            Regen_chests.LOG.info("Start regen: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
        }
        else {
            if(chestsOpened.containsKey(pos)) {
                Regen_chests.LOG.info("Block regen: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                Instant timeOpenedInstant = Instant.parse(chestsOpened.get(pos));
                Regen_chests.LOG.info(
                        "Left time: " + getLeftTime(pos));
            } else {
                Regen_chests.LOG.info("No regenChest");
            }
        }
    }
}
