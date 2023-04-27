package org.fire_ball_mods.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.fire_ball_mods.Regen_chests;
import org.fire_ball_mods.config.ChestsListDataBase;
import org.fire_ball_mods.config.LootDataBase;
import org.fire_ball_mods.model.LinkedLoot;
import org.fire_ball_mods.model.Loot;
import org.fire_ball_mods.model.RegenChestData;
import org.fire_ball_mods.util.MinecraftUtils;
import org.fire_ball_mods.util.MyVector;
import org.fire_ball_mods.util.NBTWeighted;
import org.fire_ball_mods.util.UUIDUtils;

import java.util.*;
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
                Regen_chests.INSTANCE.excludeNBTConfig.load();
                sender.sendMessage(CommandOutputs.CommandReload.goodReload());
                break;
            }
            case "loot": {
                lootCommands(Arrays.copyOfRange(args, 1, args.length), sender);
                break;
            }
            case "clear": {
                clearCommands(Arrays.copyOfRange(args, 1, args.length), sender);
                break;
            }
            case "exclude": {
                excludeAdd(sender);
                break;
            }
            case "link": {
                linksCommands(Arrays.copyOfRange(args, 1, args.length), sender);
                break;
            }
            default: {
                sender.sendMessage(CommandOutputs.Common.errorArgument());
            }
        }


    }

    private void linksCommands(String[] args, ICommandSender sender) {
        if(args.length < 1) {
            sender.sendMessage(CommandOutputs.Common.noArguments());
            return;
        }

        switch (args[0]) {
            case "add": {
                addLinkedLoot(Arrays.copyOfRange(args, 1, args.length), sender);
                return;
            }
            case "list": {
                listLinkedLoots(sender);
                return;
            }
        }

        UUID idLoot = getUUID(args[0], sender);
        if(idLoot == null) {
            return;
        }

        Loot loot = Regen_chests.INSTANCE.lootDataBase.getLoot(idLoot);
        if(!(loot instanceof LinkedLoot)) {
            sender.sendMessage(CommandOutputs.LinkOutput.errorConvertToLinkedLoot());
            return;
        }

        if(args.length < 2) {
            listLinkedLoot(idLoot, sender);
            return;
        }

        if(args[1].equals("remove")) {
            removeLinkedLoot(idLoot, Arrays.copyOfRange(args, 2, args.length), sender);
        }
    }

    private void listLinkedLoots(ICommandSender sender) {
        List<UUID> uuids = Regen_chests.INSTANCE.lootDataBase.getLinkedUUID();
        sender.sendMessage(CommandOutputs.LinkOutput.listLinkedLoots(uuids));
    }

    private void removeLinkedLoot(UUID id , String[] args, ICommandSender sender) {
        int nabor = -1;
        if(args.length >= 1) {
            nabor = getNumber(args[0], sender);
            if(nabor == -1) return;
        }
        if(nabor == -1) {
            Regen_chests.INSTANCE.lootDataBase.removeLoot(id);
        } else {
            LinkedLoot loot = (LinkedLoot) Regen_chests.INSTANCE.lootDataBase.getLoot(id);
            NBTWeighted nbt = loot.getNumberLoot(nabor);
            if(nbt == null) {
                sender.sendMessage(CommandOutputs.CommandRemove.noFoundRemove());
                return;
            }
            loot.removeNbt(nbt);
            Regen_chests.INSTANCE.linkedLootDataBase.removeNabor(id, nabor);
            Regen_chests.INSTANCE.lootDataBase.setLoot(id, loot);
        }
        sender.sendMessage(CommandOutputs.CommandRemove.goodRemove());
    }

    private void listLinkedLoot(UUID id, ICommandSender sender) {
        LinkedLoot loot = (LinkedLoot) Regen_chests.INSTANCE.lootDataBase.getLoot(id);
        List<Long> leftTime = new ArrayList<>();
        for(int i = 0; i < loot.nbts.size(); i++) {
            leftTime.add(Regen_chests.INSTANCE.handler.getLeftTimeLootNabor(loot, i));
        }
        sender.sendMessage(CommandOutputs.LinkOutput.listLinkNabors(loot, leftTime));
    }

    private void addLinkedLoot(String[] args, ICommandSender sender) {
        TileEntity tileEntity = MinecraftUtils.getViewBlock((EntityPlayerMP) sender);
        if(tileEntity == null) {
            sender.sendMessage(CommandOutputs.Common.onlyBlock());
            return;
        }
        MyVector pos = new MyVector(tileEntity.getPos(),tileEntity.getWorld().getWorldInfo().getWorldName());

        LinkedLoot linkedLoot;
        UUID idLoot = UUID.randomUUID();
        boolean isForced = false;
        int weight = Regen_chests.INSTANCE.config.DEFAULT_WEIGHT;

        if(args.length > 0) {
            if(args.length < 2) {
                weight = getWeight(args[0], sender);
                if (weight == -1) return;
            }
            if(args.length >=3) {
                idLoot = getUUID(args[1], sender);
                if(idLoot == null) return;
                if(args[2].equals("forced")) {
                    isForced = true;
                }
            }
            if(args.length >= 2) {
                if(args[1].equals("forced")) {
                    isForced = true;
                } else {
                    idLoot = getUUID(args[1], sender);
                    if (idLoot == null) return;
                }
            }

        }

        if(Regen_chests.INSTANCE.chestsDataBase.isExists(pos)) {
            idLoot = Regen_chests.INSTANCE.chestsDataBase.getChest(pos).getIdLoot();
        }

        if(Regen_chests.INSTANCE.lootDataBase.isExists(idLoot)) {
            Loot loot = Regen_chests.INSTANCE.lootDataBase.getLoot(idLoot);
            if(loot instanceof LinkedLoot) {
                linkedLoot = (LinkedLoot) loot;
            } else {
                sender.sendMessage(CommandOutputs.LinkOutput.errorConvertToLinkedLoot());
                return;
            }
        } else {
            linkedLoot = new LinkedLoot(idLoot, tileEntity.serializeNBT().getString("id"));
        }

        if(!linkedLoot.blockId.equals(tileEntity.serializeNBT().getString("id"))) {
            sender.sendMessage(CommandOutputs.LinkOutput.errorNoEqualsIdBlocks());
            return;
        }
        NBTWeighted nbt = new NBTWeighted(weight, tileEntity.serializeNBT().toString());
        if(isForced) {
            linkedLoot.addForcedNbt(nbt);
        } else {
            linkedLoot.addNbt(nbt);
        }
        Regen_chests.INSTANCE.lootDataBase.addLoot(idLoot, linkedLoot);
        sender.sendMessage(CommandOutputs.LinkOutput.goodAddToLink(idLoot));
    }

    private void excludeAdd(ICommandSender sender) {
        TileEntity block = MinecraftUtils.getViewBlock((EntityPlayerMP) sender);
        if(block == null) {
            sender.sendMessage(CommandOutputs.ExcludeOutput.errorExcludeView());
            return;
        }
        NBTTagCompound nbt = block.serializeNBT();
        List<String> keys = new ArrayList<>(nbt.getKeySet());
        Regen_chests.INSTANCE.excludeNBTConfig.addList(nbt.getString("id"), keys);
        sender.sendMessage(CommandOutputs.ExcludeOutput.goodAddExclude());
    }

    private void clearCommands(String[] args, ICommandSender sender) {
        if(args.length < 1) {
            sender.sendMessage(CommandOutputs.Common.clearErrorArgument());
            return;
        }

        switch (args[0]) {
            case "loot": {
                clearLoots(sender);
                break;
            }
            case "chest": {
                clearChests(sender);
                break;
            }
            default: {
                sender.sendMessage(CommandOutputs.Common.clearErrorArgument());
            }
        }
    }

    private void lootCommands(String[] args, ICommandSender sender) {
        if(args.length < 1) {
            sender.sendMessage(CommandOutputs.LootOutput.errorArguments());
            return;
        }

        if(args.length < 2) {
            args = new String[] {args[0], "list"};
        }

        UUID id;
        List<UUID> allIds = Regen_chests.INSTANCE.lootDataBase.getAllUUID();
        List<UUID> findIds = UUIDUtils.getUUIDsWithPartialUUID(args[0], allIds);
        if(findIds.size() != 1) {
            sender.sendMessage(CommandOutputs.LootOutput.errorId());
            return;
        }
        id = findIds.get(0);

        switch (args[1]) {
            case "list": {
                listLoot(id, sender);
                break;
            }
            case "count": {
                countChestsUseLoot(id, sender);
                break;
            }
            case "find": {
                findChestsUseLoot(id, sender);
                break;
            }
            default: {
                sender.sendMessage(CommandOutputs.Common.errorArgument());
            }
        }
    }

    private void listLoot(UUID id, ICommandSender sender) {
        Loot loot = Regen_chests.INSTANCE.lootDataBase.getLoot(id);
        if(loot instanceof LinkedLoot) {
            listLinkedLoot(id, sender);
            return;
        }
        sender.sendMessage(CommandOutputs.LootOutput.listLoot(id, loot.nbts));
    }

    private void countChestsUseLoot(UUID id, ICommandSender sender) {
        int count = Regen_chests.INSTANCE.handler.getAllChestsWithIdLoot(id).size();
        sender.sendMessage(CommandOutputs.LootOutput.countLoot(count));
    }

    private void findChestsUseLoot(UUID id, ICommandSender sender) {
        List<MyVector> chests = Regen_chests.INSTANCE.handler.getAllChestsWithIdLoot(id);
        sender.sendMessage(CommandOutputs.LootOutput.listChestsUseLoot(chests));
    }

    private void clearLoots(ICommandSender sender) {
        int deletes = Regen_chests.INSTANCE.lootDataBase.clearExcessLoots();
        sender.sendMessage(CommandOutputs.LootOutput.clearLoot(deletes));
    }

    private void clearChests(ICommandSender sender) {
        int deletes = Regen_chests.INSTANCE.chestsDataBase.clearExcessChests();
        sender.sendMessage(CommandOutputs.ChestOutput.clearChests(deletes));
    }

    private void addTileEntity(String[] args, ICommandSender sender) {

        TileEntity tileEntity = MinecraftUtils.getViewBlock((EntityPlayerMP) sender);
        if(tileEntity == null) {
            sender.sendMessage(CommandOutputs.Common.onlyBlock());
            return;
        }

        int weight = Regen_chests.INSTANCE.config.DEFAULT_WEIGHT;
        UUID idLoot = UUID.randomUUID();
        boolean isLinkAdd = false;
        if(args.length >= 1) {
            weight = getWeight(args[0], sender);
            if (weight == -1) return;
        }

        if(args.length >=2) {
            idLoot = getUUID(args[1], sender);
            if(idLoot == null) {
                return;
            }
            isLinkAdd = true;
        }

        MyVector pos = new MyVector(tileEntity.getPos(),tileEntity.getWorld().getWorldInfo().getWorldName());
        if(isLinkAdd) {
            Regen_chests.INSTANCE.chestsDataBase.addChest(pos, idLoot);
            sender.sendMessage(CommandOutputs.LinkOutput.goodChestInLink());
            return;
        }

        Loot loot = new Loot();
        boolean isExists = false;

        if(Regen_chests.INSTANCE.chestsDataBase.isExists(pos)) {
            RegenChestData regenData = Regen_chests.INSTANCE.chestsDataBase.getChest(pos);
            if(Regen_chests.INSTANCE.lootDataBase.isExists(regenData.getIdLoot())) {
                loot = Regen_chests.INSTANCE.lootDataBase.getLoot(regenData.getIdLoot());
                idLoot = regenData.getIdLoot();
                isExists = true;
            }
        }

        loot.addNbt(new NBTWeighted(weight, tileEntity.serializeNBT().toString()));

        if(isExists) {
            Regen_chests.INSTANCE.lootDataBase.setLoot(idLoot, loot);
        } else {
            Regen_chests.INSTANCE.lootDataBase.addLoot(idLoot, loot);
            Regen_chests.INSTANCE.chestsDataBase.addChest(pos, idLoot);
        }
        tileEntity.markDirty();
        sender.sendMessage(CommandOutputs.CommandAdd.goodAdd());
    }

    private void removeTileEntity(String[] args, ICommandSender sender) {
        TileEntity tileEntity = MinecraftUtils.getViewBlock((EntityPlayerMP) sender);
        int nabor = -1;
        if(args.length >= 1) {
            nabor = getNumber(args[0], sender);
            if (nabor == -1) return;
        }
        if(tileEntity == null) {
            sender.sendMessage(CommandOutputs.Common.onlyBlock());
            return;
        }
        MyVector pos = new MyVector(tileEntity.getPos(),tileEntity.getWorld().getWorldInfo().getWorldName());
        if(nabor == -1) {
            if(Regen_chests.INSTANCE.chestsDataBase.isExists(pos)) {
                Regen_chests.INSTANCE.chestsDataBase.removeChestAndZeroLoot(pos);
                sender.sendMessage(CommandOutputs.CommandRemove.goodRemove());
            }
            else {
                sender.sendMessage(CommandOutputs.CommandRemove.noFoundRemove());
            }
            return;
        }

        Loot loot;
        UUID idLoot;
        if(Regen_chests.INSTANCE.chestsDataBase.isExists(pos) && Regen_chests.INSTANCE.lootDataBase.isExists(Regen_chests.INSTANCE.chestsDataBase.getChest(pos).getIdLoot())) {
            idLoot = Regen_chests.INSTANCE.chestsDataBase.getChest(pos).getIdLoot();
            loot = Regen_chests.INSTANCE.lootDataBase.getLoot(idLoot);
        }
        else {
            sender.sendMessage(CommandOutputs.CommandRemove.noFoundRemove());
            return;
        }
        NBTWeighted nbt = loot.getNumberLoot(nabor);

        if(nbt == null) {
            sender.sendMessage(CommandOutputs.CommandRemove.noFoundRemove());
            return;
        }
        loot.removeNbt(nbt);

        Regen_chests.INSTANCE.lootDataBase.addLoot(idLoot, loot);
        sender.sendMessage(CommandOutputs.CommandRemove.goodRemove());
    }

    private void printTileEntity(String[] args, ICommandSender sender) {
        int page = 1;
        if(args.length >= 1) {
            page = getNumber(args[0], sender);
            if (page == -1) return;
        }
        HashMap<MyVector,String> items = new HashMap<>();
        Regen_chests.INSTANCE.chestsDataBase.getData().forEach((MyVector pos, RegenChestData data) -> {
            items.put(pos,data.lastTimeOpen);
        });

        sender.sendMessage(CommandOutputs.CommandList.listTiles(items,page));
    }

    private void regenTileEntity(String[] args, ICommandSender sender) {
        int nabor = -1;
        if(args.length >= 1) {
            nabor = getNumber(args[0], sender);
            if(nabor == -1) return;
        }
        TileEntity tileEntity = MinecraftUtils.getViewBlock((EntityPlayerMP) sender);
        if(tileEntity == null) {
            sender.sendMessage(CommandOutputs.Common.onlyBlock());
            return;
        }
        MyVector pos = new MyVector(tileEntity.getPos(),tileEntity.getWorld().getWorldInfo().getWorldName());
        if(!Regen_chests.INSTANCE.chestsDataBase.isExists(pos)) {
            sender.sendMessage(CommandOutputs.Common.noRegenBlock());
            return;
        }
        if(nabor == -1) {
            Regen_chests.INSTANCE.handler.regenerateChest(tileEntity);
            sender.sendMessage(CommandOutputs.CommandRegen.goodRegen());
        }
        else {
            Loot loot = null;

            if(Regen_chests.INSTANCE.lootDataBase.isExists(Regen_chests.INSTANCE.chestsDataBase.getChest(pos).getIdLoot())) {
                loot = Regen_chests.INSTANCE.lootDataBase.getLoot(Regen_chests.INSTANCE.chestsDataBase.getChest(pos).getIdLoot());
            }
            else {
                sender.sendMessage(CommandOutputs.Common.error("ОШИБКА!!! Неверный UUID лута."));
                return;
            }

            NBTWeighted nbt = loot.getNumberLoot(nabor);
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
            Regen_chests.INSTANCE.handler.regenerateChest(tileEntity,nbtTag);
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
        if(!Regen_chests.INSTANCE.chestsDataBase.isExists(pos)) {
            sender.sendMessage(CommandOutputs.Common.noRegenBlock());
            return;
        }
        long time = Regen_chests.INSTANCE.handler.getLeftTime(pos);
        RegenChestData data = Regen_chests.INSTANCE.chestsDataBase.getChest(pos);
        sender.sendMessage(CommandOutputs.CommandInfo.infoTile(time,data));
    }

    private UUID getUUID(String particleUUID, ICommandSender sender) {
        List<UUID> allIds = Regen_chests.INSTANCE.lootDataBase.getAllUUID();
        List<UUID> findIds = UUIDUtils.getUUIDsWithPartialUUID(particleUUID, allIds);
        if(findIds.size() != 1) {
            if(sender != null) {
                sender.sendMessage(CommandOutputs.LootOutput.errorId());
            }
            return null;
        }
        return findIds.get(0);
    }

    private int getWeight(String weightStr, ICommandSender sender) {
        int weight;
        try {
            weight = parseInt(weightStr);
        } catch (Exception e) {
            weight = -1;
        }
        if(weight == -1) {
            if(!weightStr.equals("default")) {
                if(sender != null) {
                    sender.sendMessage(CommandOutputs.Common.errorNumber());
                }
                return -1;
            }
            weight = Regen_chests.INSTANCE.config.DEFAULT_WEIGHT;
        }
        return weight;
    }

    private int getNumber(String weightStr, ICommandSender sender) {
        int number = -1;
        try {
            number = parseInt(weightStr);
        } catch (Exception e) {
            sender.sendMessage(CommandOutputs.Common.errorNumber());
        }
        return number;
    }



}
