package org.fire_ball_mods.commands;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.fire_ball_mods.Regen_chests;
import org.fire_ball_mods.model.LinkedLoot;
import org.fire_ball_mods.model.RegenChestData;
import org.fire_ball_mods.util.ComponentBuilder;
import org.fire_ball_mods.util.MyVector;
import org.fire_ball_mods.util.NBTWeighted;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class CommandOutputs {

    public static class LinkOutput {
        public static ITextComponent goodChestInLink() {
            return ComponentBuilder.builder("Блок успешно добавлен в связь.").color(TextFormatting.GOLD).build();
        }
        public static ITextComponent errorConvertToLinkedLoot() {
            return Common.error("Лут должен быть связанным!");
        }
        public static ITextComponent errorNoEqualsIdBlocks() {
            return Common.error("Не соответствие целевого и исходного блока!");
        }
        public static ITextComponent goodAddToLink(UUID id) {
            return ComponentBuilder.builder("Nbt набор успешно добавлен в связь: ").color(TextFormatting.GOLD)
                    .append(TextFormatting.GREEN, id.toString())
                    .build();
        }

        public static ITextComponent listLinkedLoots(List<UUID> loots) {
            ComponentBuilder builder = ComponentBuilder.builder("Список связанных наборов: ")
                    .bold(true)
                    .color(TextFormatting.YELLOW);
            for(int i = 1; i <= loots.size(); i++) {
                ComponentBuilder uuid = ComponentBuilder.builder(loots.get(i-1).toString())
                                .color(TextFormatting.GREEN)
                                .bold(true)
                                .click(ClickEvent.Action.RUN_COMMAND, "/regenchest link " + loots.get(i-1).toString())
                                .hover(HoverEvent.Action.SHOW_TEXT, ComponentBuilder.builder("Посмотреть список наборов")
                                        .color(TextFormatting.GREEN)
                                        .build());
                builder.append("\n")
                        .append(TextFormatting.YELLOW, i + ". ")
                        .append(uuid)
                        .append(TextFormatting.YELLOW, ";");
            }
            return builder.build();
        }

        public static ITextComponent listLinkNabors(LinkedLoot loot, List<Long> leftTimes) {
            ComponentBuilder builder = ComponentBuilder.builder("UUID набора: ").color(TextFormatting.YELLOW)
                    .bold(true);
            builder.append(TextFormatting.GREEN ,loot.uuid.toString())
                    .append(TextFormatting.YELLOW, "\nID блока: ")
                    .append(TextFormatting.GREEN, loot.blockId)
                    .append(TextFormatting.YELLOW, "\nСписок несгораемых наборов: ");
            for(int i = 0; i < loot.getForcedNbts().size(); i++) {
                builder.append("\n")
                        .append(TextFormatting.YELLOW, String.valueOf(i))
                        .append(TextFormatting.GREEN, " -> ")
                        .append(TextFormatting.YELLOW, loot.getForcedNbts().get(i).itemWeight + ";");
            }
            builder.append(TextFormatting.YELLOW, "\nСписок обычных наборов: ");
            for(int i = 0; i < loot.nbts.size(); i++) {
                int count = i + loot.getForcedNbts().size();
                builder.append("\n")
                        .append(TextFormatting.YELLOW, String.valueOf(count))
                        .append(TextFormatting.GREEN, " -> ")
                        .append(TextFormatting.YELLOW, loot.nbts.get(i).itemWeight + ": ");
                if(leftTimes.get(i) == -1) {
                    builder.append(TextFormatting.AQUA,"Не использовался;");
                } else if (leftTimes.get(i) == 0) {
                    builder.append(TextFormatting.GREEN,"ГОТОВ;");
                } else {
                    builder.append(TextFormatting.GOLD, leftTimes.get(i) + " сек.;");
                }

            }
            return builder.build();
        }
    }

    public static class ExcludeOutput {
        public static ITextComponent errorExcludeView() {
            return Common.error("Надо смотреть на блок, который имеет TileEntity!");
        }
        public static ITextComponent goodAddExclude() {
            return ComponentBuilder.builder("Успешно добавлено в исключение nbt.").color(TextFormatting.GOLD).build();
        }
    }

    public static class LootOutput {
        public static ITextComponent errorArguments() {
            return Common.error("Необходим аргумент и начало id лута!");
        }

        public static ITextComponent errorId() {
            return Common.error("Неточный id лута, лут не найден, либо найдено больше одного!");
        }

        public static ITextComponent clearLoot(int count) {
            return ComponentBuilder.builder("Удалено лишнего лута: ").color(TextFormatting.GREEN)
                    .append(TextFormatting.GOLD, String.valueOf(count))
                    .build();
        }

        public static ITextComponent countLoot(int count) {
            return ComponentBuilder.builder("Блоков, использующих этот лут: ").color(TextFormatting.GREEN)
                    .append(TextFormatting.GOLD, String.valueOf(count))
                    .build();
        }

        public static ITextComponent listChestsUseLoot(List<MyVector> blocks) {
            ComponentBuilder builder = ComponentBuilder.builder("Блоки: ").color(TextFormatting.YELLOW).bold(true);
            for(int i = 0; i < blocks.size(); i++) {
                MyVector block = blocks.get(i);
                ComponentBuilder row = ComponentBuilder.builder("\n" + (i+1) +". ").color(TextFormatting.YELLOW).bold(true);
                row.append(TextFormatting.GREEN, block.getX() + ", " + block.getY() + ", " + block.getZ() + " -> " + block.getWorld());
                builder.append(row);
            }
            return builder.build();
        }

        public static ITextComponent listLoot(UUID id, List<NBTWeighted> nbts) {
            ComponentBuilder builder = ComponentBuilder.builder("UUID лута: ").color(TextFormatting.YELLOW).bold(true);
            builder.append(TextFormatting.GREEN, id.toString() + ";");
            builder.append(TextFormatting.YELLOW, "\nВсего nbt-наборов: " + nbts.size());
            ComponentBuilder listNabors = builder.child(TextFormatting.GREEN, "\nСписок наборов (номер -> вес): ");
            int count = 0;
            for(NBTWeighted nbt : nbts) {
                listNabors.append(TextFormatting.YELLOW, "\n" + count + ". -> " + nbt.itemWeight+";");
                count++;
            }
            return listNabors.parent().build();
        }
    }

    public static class ChestOutput {
        public static ITextComponent clearChests(int count) {
            return ComponentBuilder.builder("Удалено лишних сундуков: ").color(TextFormatting.GREEN)
                    .append(TextFormatting.GOLD, String.valueOf(count))
                    .build();
        }
    }

    public static class Common {

        public static ITextComponent clearErrorArgument() {
            return error("Необходимо указать класс! (loot или chest)");
        }

        public static ITextComponent onlyPlayer() {
            return error("Только игрок");
        }

        public static ITextComponent noArguments() {
            return error("Необходим хотя бы один аргумент.");
        }

        public static ITextComponent errorArgument() {
            return error("Неверный аргумент.");
        }

        public static ITextComponent errorNumber() {
            return error("Неверный ввод числа!!!");
        }

        public static ITextComponent onlyBlock() {
            return error("Необходимо смотреть на контейнер.");
        }

        public static ITextComponent noRegenBlock() {
            return error("Это не восстанавливаемый блок.");
        }

        public static ITextComponent noNomerNabor() {
            return error("Неверный номар nbt-набора.");
        }
        public static ITextComponent error(String error) {
            return ComponentBuilder.builder(error).color(TextFormatting.RED).build();
        }
    }

    public static class CommandAdd {
        public static ITextComponent goodAdd() {
            return ComponentBuilder.builder("Успешно добавлено.").color(TextFormatting.GOLD).build();
        }
    }

    public static class CommandRegen {
        public static ITextComponent goodRegen() {
            return ComponentBuilder.builder("Успешно восстановлено.").color(TextFormatting.GOLD).build();
        }
    }

    public static class CommandRemove {
        public static ITextComponent goodRemove() {
            return ComponentBuilder.builder("Успешно удалено.").color(TextFormatting.GOLD).build();
        }

        public static ITextComponent noFoundRemove() {
            return ComponentBuilder.builder("Не найден объект для удаления.").color(TextFormatting.GOLD).build();
        }
    }

    public static class CommandList {
        public static ITextComponent listTiles(HashMap<MyVector,String> objects, int page, int countInPage) {
            ComponentBuilder builder = ComponentBuilder.builder("Список восстанавливаемых объектов.").color(TextFormatting.YELLOW).append("\n");
            List<MyVector> sortedList = objects.entrySet().stream()
                    .sorted(
                            Comparator
                                    .comparing(
                                            (Map.Entry<MyVector,String> object) ->
                                                    Instant.parse(object.getValue()))
                                    .reversed())
                    .map(Map.Entry::getKey).collect(Collectors.toList());
            if(page > Math.ceil(sortedList.size()/(float)countInPage) || page<1) {
                page = 1;
            }
            builder.append(TextFormatting.YELLOW,
                    "Страница " + page + " из " + (int)Math.ceil(sortedList.size()/(float)countInPage)+": ")
                    .append("\n");
            int minElement = (page-1)*countInPage;
            int maxElement = minElement+(Math.min(countInPage, sortedList.size()-minElement));
            if(sortedList.size()>countInPage) {
                sortedList = sortedList.subList(minElement,maxElement);
            }
            int count = 0;
            for (MyVector pos : sortedList) {
                String command = Regen_chests.INSTANCE.config.TP_COMMAND;
                command = command.replace("[x]",Integer.toString(pos.getX()));
                command = command.replace("[y]",Integer.toString(pos.getY()));
                command = command.replace("[z]",Integer.toString(pos.getZ()));
                command = command.replace("[world]",pos.getWorld());
                ComponentBuilder rowBuilder = ComponentBuilder.builder((minElement+1+count) + ". ")
                        .color(TextFormatting.GOLD)
                        .click(ClickEvent.Action.RUN_COMMAND,
                                "/" + command)
                        .hover(HoverEvent.Action.SHOW_TEXT,
                                ComponentBuilder.builder("tp").color(TextFormatting.GREEN).build())
                        .child(TextFormatting.YELLOW,"("+pos.getX()+", "+pos.getY()+", "+pos.getZ()+ ", " + pos.getWorld() + ")")
                        .append(" -> ");
                if(Regen_chests.INSTANCE.handler.isRegen(pos)) {
                    rowBuilder.append(TextFormatting.GREEN,"ГОТОВ");
                }
                else {
                    rowBuilder.append(TextFormatting.RED,"НЕ ГОТОВ: "
                            + Regen_chests.INSTANCE.handler.getLeftTime(pos)
                            + " сек.");
                }
                rowBuilder.append(TextFormatting.YELLOW,";");
                builder.append(rowBuilder.parent().append("\n"));
                count++;
            }
            return builder.build();
        }

        public static ITextComponent listTiles(HashMap<MyVector,String> objects, int page) {
            return listTiles(objects,page,7);
        }
    }

    public static class CommandInfo {
        public static ITextComponent infoTile(long time, RegenChestData chest) {
            ComponentBuilder builder = ComponentBuilder.builder("Информация: ").color(TextFormatting.GREEN)
                    .bold(true)
                    .append(TextFormatting.YELLOW, "\nОсталось времени: ");
            if(time == 0) {
                builder.append(TextFormatting.GREEN, "0 сек.");
            }
            else if(time == -1) {
                builder.append(TextFormatting.DARK_AQUA, "Ещё не открывался.");
            } else {
                builder.append(TextFormatting.YELLOW,  time +" сек.");
            }
            builder.append(TextFormatting.YELLOW, "\nUUID лута: ");
            String id = chest.getIdLoot().toString();
            ComponentBuilder idBuilder =  builder.child(TextFormatting.GREEN,  id + ".")
                    .click(ClickEvent.Action.RUN_COMMAND, "/regenchest loot " + id + " list")
                    .hover(
                            HoverEvent.Action.SHOW_TEXT,
                            ComponentBuilder.builder("Показать список наборов")
                                    .color(TextFormatting.YELLOW)
                                    .build());

            return idBuilder.parent().build();
        }
    }

    public static class CommandReload {
        public static ITextComponent goodReload() {
            return ComponentBuilder.builder("Успешно перезагружено.").color(TextFormatting.GREEN).build();
        }
    }
}
