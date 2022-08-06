package org.fire_ball.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
import org.fire_ball.Regen_chests;
import org.fire_ball.util.MinecraftUtils;
import org.fire_ball.util.MyVector;
import org.fire_ball.util.NBTWeighted;
import org.fire_ball.util.RegenChest;

import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

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
        if(!(sender instanceof EntityPlayerMP)) {
            sender.sendMessage(CommandOutputs.Common.onlyPlayer());
            return;
        }
        if(args.length < 1) {
            sender.sendMessage(CommandOutputs.Common.noArguments());
            return;
        }
        switch (args[0]) {
            case "add": {
                addTileEntity(Arrays.copyOfRange(args, 1, args.length),sender);
                break;
            }
            case "list": {
                printTileEntity(Arrays.copyOfRange(args, 1, args.length),sender);
                break;
            }
            case "remove": {
                removeTileEntity(Arrays.copyOfRange(args, 1, args.length),sender);
                break;
            }
            case "regen": {
                regenTileEntity(Arrays.copyOfRange(args, 1, args.length), sender);
                break;
            }
            case "info": {
                infoTileEntity(sender);
                break;
            }
            case "reload" : {
                Regen_chests.INSTANCE.config.load();
                Regen_chests.INSTANCE.regenChestsConfig.load();
                Regen_chests.INSTANCE.regenChestsDataBase.load();
                sender.sendMessage(CommandOutputs.CommandReload.goodReload());
                break;
            }
            default: {
                sender.sendMessage(CommandOutputs.Common.errorArgument());
            }
        }


    }

    private void addTileEntity(String[] args, ICommandSender sender) {

        TileEntity tileEntity = MinecraftUtils.getViewBlock((EntityPlayerMP) sender);
        int weight = Regen_chests.INSTANCE.config.DEFAULT_WEIGHT;
        if(args.length >= 1) {
            try {
                weight = parseInt(args[0]);
            } catch (NumberInvalidException e) {
                sender.sendMessage(CommandOutputs.Common.errorNumber(new TextComponentTranslation(e.getMessage(),args[0])));
                return;
            }
        }
        if(tileEntity == null) {
            sender.sendMessage(CommandOutputs.Common.onlyBlock());
            return;
        }
        RegenChest regenChest;
        MyVector pos = new MyVector(tileEntity.getPos(),tileEntity.getWorld().getWorldInfo().getWorldName());
        if(Regen_chests.INSTANCE.regenChestsConfig.isExists(pos)) {
            regenChest = Regen_chests.INSTANCE.regenChestsConfig.getChest(pos);
        }
        else {
            regenChest = new RegenChest(new ArrayList<>());
        }
        regenChest.addNbt(new NBTWeighted(weight, tileEntity.serializeNBT().toString()));

        Regen_chests.INSTANCE.regenChestsConfig.addChest(pos,regenChest);
        tileEntity.markDirty();
        sender.sendMessage(CommandOutputs.CommandAdd.goodAdd());
    }

    private void removeTileEntity(String[] args, ICommandSender sender) {
        TileEntity tileEntity = MinecraftUtils.getViewBlock((EntityPlayerMP) sender);
        int nabor = -1;
        if(args.length >= 1) {
            try {
                nabor = parseInt(args[0]);
            } catch (NumberInvalidException e) {
                sender.sendMessage(CommandOutputs.Common.errorNumber(new TextComponentTranslation(e.getMessage(),args[0])));
                return;
            }
        }
        if(tileEntity == null) {
            sender.sendMessage(CommandOutputs.Common.onlyBlock());
            return;
        }
        RegenChest regenChest;
        MyVector pos = new MyVector(tileEntity.getPos(),tileEntity.getWorld().getWorldInfo().getWorldName());
        if(nabor == -1) {
            if(Regen_chests.INSTANCE.regenChestsConfig.isExists(pos)) {
                Regen_chests.INSTANCE.regenChestsConfig.removeChest(pos);
                sender.sendMessage(CommandOutputs.CommandRemove.goodRemove());
            }
            else {
                sender.sendMessage(CommandOutputs.CommandRemove.noFoundRemove());
            }
            return;
        }
        if(Regen_chests.INSTANCE.regenChestsConfig.isExists(pos)) {
            regenChest = Regen_chests.INSTANCE.regenChestsConfig.getChest(pos);
        }
        else {
            sender.sendMessage(CommandOutputs.CommandRemove.noFoundRemove());
            return;
        }
        NBTWeighted nbt = regenChest.getNbtWeight(nabor);

        if(nbt == null) {
            sender.sendMessage(CommandOutputs.CommandRemove.noFoundRemove());
            return;
        }
        regenChest.removeNbt(nbt);

        Regen_chests.INSTANCE.regenChestsConfig.addChest(pos,regenChest);
        sender.sendMessage(CommandOutputs.CommandRemove.goodRemove());
    }

    private void printTileEntity(String[] args, ICommandSender sender) {
        int page = 1;
        if(args.length >= 1) {
            try {
                page = parseInt(args[0]);
            } catch (NumberInvalidException e) {
                sender.sendMessage(CommandOutputs.Common.errorNumber(new TextComponentTranslation(e.getMessage(),args[0])));
                return;
            }
        }
        HashMap<MyVector,String> items = Regen_chests.INSTANCE.regenChestsDataBase.getData();
        items.putAll(Regen_chests.INSTANCE.regenChestsConfig.chests.keySet().stream()
                .map(chest -> new AbstractMap.SimpleEntry<>(chest, Instant.MIN.toString()))
                        .filter(object -> !items.containsKey(object.getKey()))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,AbstractMap.SimpleEntry::getValue)));
        sender.sendMessage(CommandOutputs.CommandList.listTiles(items,page));
    }

    private void regenTileEntity(String[] args, ICommandSender sender) {
        int nabor = -1;
        if(args.length >= 1) {
            try {
                nabor = parseInt(args[0]);
            } catch (NumberInvalidException e) {
                sender.sendMessage(CommandOutputs.Common.errorNumber(new TextComponentTranslation(e.getMessage(),args[0])));
                return;
            }
        }
        TileEntity tileEntity = MinecraftUtils.getViewBlock((EntityPlayerMP) sender);
        if(tileEntity == null) {
            sender.sendMessage(CommandOutputs.Common.onlyBlock());
            return;
        }
        MyVector pos = new MyVector(tileEntity.getPos(),tileEntity.getWorld().getWorldInfo().getWorldName());
        if(!Regen_chests.INSTANCE.regenChestsConfig.isExists(pos)) {
            sender.sendMessage(CommandOutputs.Common.noRegenBlock());
            return;
        }
        if(nabor == -1) {
            Regen_chests.INSTANCE.regenChestsDataBase.regenerateChest(tileEntity);
            sender.sendMessage(CommandOutputs.CommandRegen.goodRegen());
        }
        else {
            NBTWeighted nbt = Regen_chests.INSTANCE.regenChestsConfig.getChest(pos).getNbtWeight(nabor);
            if(nbt == null) {
                sender.sendMessage(CommandOutputs.Common.noNomerNabor());
                return;
            }
            NBTTagCompound nbtTag;
            try {
                nbtTag = JsonToNBT.getTagFromJson(nbt.getNbt());
            }
            catch (Exception e) {
                sender.sendMessage(CommandOutputs.Common.error("ОШИБКА!!! Неверный набор."));
                e.printStackTrace();
                return;
            }
            Regen_chests.INSTANCE.regenChestsDataBase.regenerateChest(tileEntity,nbtTag);
            sender.sendMessage(CommandOutputs.CommandRegen.goodRegen());
        }
    }

    private void infoTileEntity(ICommandSender sender) {
        TileEntity tileEntity = MinecraftUtils.getViewBlock((EntityPlayerMP) sender);
        if(tileEntity == null) {
            sender.sendMessage(CommandOutputs.Common.onlyBlock());
            return;
        }
        MyVector pos = new MyVector(tileEntity.getPos(),tileEntity.getWorld().getWorldInfo().getWorldName());
        if(!Regen_chests.INSTANCE.regenChestsConfig.isExists(pos)) {
            sender.sendMessage(CommandOutputs.Common.noRegenBlock());
            return;
        }
        long time = Regen_chests.INSTANCE.regenChestsDataBase.getLeftTime(pos);
        RegenChest chest = Regen_chests.INSTANCE.regenChestsConfig.getChest(pos);
        sender.sendMessage(CommandOutputs.CommandInfo.infoTile(time,chest));
    }



}
