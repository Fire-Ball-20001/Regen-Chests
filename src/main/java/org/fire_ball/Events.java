package org.fire_ball;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.fire_ball.util.MinecraftUtils;
import org.fire_ball.util.MyVector;


public class Events {
    @SubscribeEvent
    public void onPlayerOpenInventory(PlayerContainerEvent event) {

        TileEntity block = MinecraftUtils.getViewBlock((EntityPlayerMP) event.getEntityPlayer());

        if(block == null) {
            return;
        }
        MyVector pos = new MyVector(block.getPos(),block.getWorld().getWorldInfo().getWorldName());
        boolean isRegen = Regen_chests.INSTANCE.regenChestsDataBase.isRegen(pos);
        if(Regen_chests.INSTANCE.config.IS_DEBUG) {
            Regen_chests.INSTANCE.regenChestsDataBase.printDebug(isRegen,pos);
        }
        if(isRegen) {
            Regen_chests.INSTANCE.regenChestsDataBase.regenerateChest(block);
        }
    }

}
