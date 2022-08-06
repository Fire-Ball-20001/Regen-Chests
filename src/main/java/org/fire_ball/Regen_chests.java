package org.fire_ball;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import org.fire_ball.commands.CommandExecutor;
import org.fire_ball.config.MainConfig;
import org.fire_ball.config.RegenChestsConfig;
import org.fire_ball.config.RegenChestsDataBase;

@Mod(
        modid = Regen_chests.MOD_ID,
        name = Regen_chests.MOD_NAME,
        version = Regen_chests.VERSION,
        acceptableRemoteVersions = "*"
)
public class Regen_chests {

    public static final String MOD_ID = "regen_chests";
    public static final String MOD_NAME = "Regen Chests";
    public static final String VERSION = "0.9.3";

    public static final String MAIN_FOLDER="config/"+MOD_ID+"/";
    public static Logger LOG;

    public MainConfig config = new MainConfig("","config.cfg");
    public RegenChestsDataBase regenChestsDataBase = new RegenChestsDataBase("/data/","regenChests.json");
    public RegenChestsConfig regenChestsConfig = new RegenChestsConfig("","regenChests.cfg");

    @Mod.Instance(MOD_ID)
    public static Regen_chests INSTANCE;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        LOG = event.getModLog();
        MinecraftForge.EVENT_BUS.register(new Events());
    }


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        config.load();
        regenChestsDataBase.load();
        regenChestsConfig.load();
    }


    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandExecutor());
    }

    @Mod.EventHandler
    public void stopServer(FMLServerStoppingEvent event) {
        regenChestsDataBase.save();
        regenChestsConfig.save();
    }

    /**
     * Forge will automatically look up and bind blocks to the fields in this class
     * based on their registry name.
     */
    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Blocks {
      /*
          public static final MySpecialBlock mySpecialBlock = null; // placeholder for special block below
      */
    }

    /**
     * Forge will automatically look up and bind items to the fields in this class
     * based on their registry name.
     */
    @GameRegistry.ObjectHolder(MOD_ID)
    public static class Items {
      /*
          public static final ItemBlock mySpecialBlock = null; // itemblock for the block above
          public static final MySpecialItem mySpecialItem = null; // placeholder for special item below
      */
    }

    /**
     * This is a special class that listens to registry events, to allow creation of mod blocks and items at the proper time.
     */
    @Mod.EventBusSubscriber
    public static class ObjectRegistryHandler {
        /**
         * Listen for the register event for creating custom items
         */
        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
           /*
             event.getRegistry().register(new ItemBlock(Blocks.myBlock).setRegistryName(MOD_ID, "myBlock"));
             event.getRegistry().register(new MySpecialItem().setRegistryName(MOD_ID, "mySpecialItem"));
            */
        }

        /**
         * Listen for the register event for creating custom blocks
         */
        @SubscribeEvent
        public static void addBlocks(RegistryEvent.Register<Block> event) {
           /*
             event.getRegistry().register(new MySpecialBlock().setRegistryName(MOD_ID, "mySpecialBlock"));
            */
        }
    }
    /* EXAMPLE ITEM AND BLOCK - you probably want these in separate files
    public static class MySpecialItem extends Item {

    }

    public static class MySpecialBlock extends Block {

    }
    */
}
