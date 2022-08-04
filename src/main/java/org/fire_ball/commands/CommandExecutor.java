package org.fire_ball.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.fire_ball.Regen_chests;
import org.fire_ball.util.MyVector;
import org.fire_ball.util.RegenChest;

import java.util.HashMap;

public class CommandExecutor extends CommandBase {
    @Override
    public String getName() {
        return "regenchest";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return new TextComponentTranslation("regen_chests.commands.usage").getFormattedText();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof EntityPlayer)) {
            sender.sendMessage(CommandOutputs.Common.onlyPlayer());
            return;
        }
        if(args.length < 1) {
            sender.sendMessage(CommandOutputs.Common.noArguments());
            return;
        }

        if(!args[0].equals("add") && !args[0].equals("remove")) {
            sender.sendMessage(CommandOutputs.Common.errorArgument());
            return;
        }

        TileEntityChest chest = getChest(sender);

        if(chest == null) {
            return;
        }

        HashMap<Integer, String> items = new HashMap<>();
        int count = 0;
        for(int i = 0; i < chest.getSizeInventory();i++) {
            if(count>26) break;
            if(chest.getStackInSlot(i).isEmpty()) {
                continue;
            }
            items.put(i,chest.getStackInSlot(i).serializeNBT().toString());
            count++;
        }
        RegenChest regenChest = new RegenChest(true,5,items);

        Regen_chests.INSTANCE.regenChestsConfig.addChest(new MyVector(chest.getPos()),regenChest);
        chest.clear();
        chest.markDirty();
        sender.sendMessage(new TextComponentString("Good"));
    }

    private TileEntityChest getChest(ICommandSender sender) {
        RayTraceResult result = ((EntityPlayer) sender).rayTrace(5,1);
        if(result == null) {
            sender.sendMessage(new TextComponentString("Error rayTrace"));
            return null;
        }

        if(result.typeOfHit != RayTraceResult.Type.BLOCK) {
            sender.sendMessage(new TextComponentString("Error no Block"));
            return null;
        }

        TileEntity block = sender.getEntityWorld().getTileEntity(result.getBlockPos());

        if(block == null) {
            sender.sendMessage(new TextComponentString("Error no TileEntity"));
            return null;
        }

        if(!(block instanceof TileEntityChest)) {
            sender.sendMessage(new TextComponentString("Error"));
            return null;
        }
        return (TileEntityChest) block;
    }
}
