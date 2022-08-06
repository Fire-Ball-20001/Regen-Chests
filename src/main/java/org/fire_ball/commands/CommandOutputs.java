package org.fire_ball.commands;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import org.fire_ball.Regen_chests;
import org.fire_ball.util.ComponentBuilder;
import org.fire_ball.util.MyVector;
import org.fire_ball.util.NBTWeighted;
import org.fire_ball.util.RegenChest;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandOutputs {
    public static class Common {
        public static ITextComponent onlyPlayer() {
            return ComponentBuilder.builder("Только игрок").color(TextFormatting.RED).build();
        }

        public static ITextComponent noArguments() {
            return ComponentBuilder.builder("Необходим хотя бы один аргумент.").color(TextFormatting.RED).build();
        }

        public static ITextComponent errorArgument() {
            return ComponentBuilder.builder("Неверный аргумент.").color(TextFormatting.RED).build();
        }

        public static ITextComponent errorNumber(ITextComponent text) {
            return ComponentBuilder.builder(text).color(TextFormatting.RED).build();
        }

        public static ITextComponent onlyBlock() {
            return ComponentBuilder.builder("Необходимо смотреть на контейнер.").color(TextFormatting.RED).build();
        }

        public static ITextComponent noRegenBlock() {
            return ComponentBuilder.builder("Это не восстанавливаемый блок.").color(TextFormatting.RED).build();
        }

        public static ITextComponent noNomerNabor() {
            return ComponentBuilder.builder("Неверный номар nbt-набора.").color(TextFormatting.RED).build();
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
                ComponentBuilder rowBuilder = ComponentBuilder.builder((minElement+1+count) + ". ")
                        .color(TextFormatting.GOLD)
                        .click(ClickEvent.Action.RUN_COMMAND,
                                "/" + Regen_chests.INSTANCE.config.TP_COMMAND+" " + pos.getX()+" "+pos.getY()+" "+pos.getZ())
                        .hover(HoverEvent.Action.SHOW_TEXT,
                                ComponentBuilder.builder("tp").color(TextFormatting.GREEN).build())
                        .child(TextFormatting.YELLOW,"("+pos.getX()+", "+pos.getY()+", "+pos.getZ()+")")
                        .append(" -> ");
                if(Regen_chests.INSTANCE.regenChestsDataBase.isRegen(pos)) {
                    rowBuilder.append(TextFormatting.GREEN,"ГОТОВ");
                }
                else {
                    rowBuilder.append(TextFormatting.RED,"НЕ ГОТОВ: "
                            + Regen_chests.INSTANCE.regenChestsDataBase.getLeftTime(pos)
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
        public static ITextComponent infoTile(long time, RegenChest chest) {
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
            builder.append(TextFormatting.YELLOW, "\nВсего nbt-наборов: " + chest.nbts.size());
            ComponentBuilder listNabors = builder.child(TextFormatting.GREEN, "\nСписок наборов (номер -> вес): ");
            int count = 0;
            for(NBTWeighted nbt : chest.nbts) {
                listNabors.append(TextFormatting.YELLOW, "\n" + count + ". -> " + nbt.itemWeight+";");
                count++;
            }
            return listNabors.parent().build();
        }
    }

    public static class CommandReload {
        public static ITextComponent goodReload() {
            return ComponentBuilder.builder("Успешно перезагружено.").color(TextFormatting.GREEN).build();
        }
    }
}
