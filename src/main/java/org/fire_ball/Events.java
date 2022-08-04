package org.fire_ball;

import net.minecraft.inventory.ContainerChest;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class Events {
    @SubscribeEvent
    public void onPlayerOpenInventory(PlayerContainerEvent event) {
        if(!(event.getContainer() instanceof ContainerChest)) return;
        Regen_chests.INSTANCE.regenChestsDataBase.regenerateChest((ContainerChest) event.getContainer());
    }
}
