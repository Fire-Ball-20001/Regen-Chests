package org.fire_ball.commands;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.fire_ball.util.ComponentBuilder;

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

        public static ITextComponent errorDirection() {
            return ComponentBuilder.builder("Неверный аргумент.").color(TextFormatting.RED).build();
        }

        public static ITextComponent errorBlock() {
            return ComponentBuilder.builder("Неверный аргумент.").color(TextFormatting.RED).build();
        }
    }
}
