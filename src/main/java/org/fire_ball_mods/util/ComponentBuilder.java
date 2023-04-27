package org.fire_ball_mods.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class ComponentBuilder {
    protected ITextComponent component;
    protected ComponentBuilder parent;

    public ComponentBuilder(String value){
        this.component = new TextComponentString(value);
    }

    public ComponentBuilder(ITextComponent value){
        this.component = value;
    }

    public static ComponentBuilder builder(ITextComponent value){
        return new ComponentBuilder(value);
    }

    public static ComponentBuilder builder(String value){
        return new ComponentBuilder(value);
    }

    protected ComponentBuilder(String value, ComponentBuilder parent){
        this.component = new TextComponentString(value);
        this.parent = parent;
    }

    public ComponentBuilder click(ClickEvent.Action action, String arg){
        component.getStyle().setClickEvent(new ClickEvent(action, arg));

        return this;
    }

    public ComponentBuilder hover(HoverEvent.Action action, ITextComponent arg){
        component.getStyle().setHoverEvent(new HoverEvent(action, arg));

        return this;
    }

    public ComponentBuilder hoverItem(ItemStack itemStack){
        NBTTagCompound nbttagcompound = itemStack.writeToNBT(new NBTTagCompound());

        return hover(HoverEvent.Action.SHOW_ITEM, new TextComponentString(nbttagcompound.toString()));
    }

    public ComponentBuilder hoverItem(String nameOrId) {
        Item item = Item.getByNameOrId(nameOrId);

        if (item == null)
            return this;

        ItemStack itemStack = new ItemStack(item);
        itemStack.setCount(1);

        return hoverItem(itemStack);
    }

    public ComponentBuilder color(TextFormatting value){
        component.getStyle().setColor(value);

        return this;
    }

    public ComponentBuilder italic(boolean value){
        component.getStyle().setItalic(value);

        return this;
    }

    public ComponentBuilder bold(boolean value){
        component.getStyle().setBold(value);

        return this;
    }

    public ComponentBuilder strikethrough(boolean value){
        component.getStyle().setStrikethrough(value);

        return this;
    }

    public ComponentBuilder underlined(boolean value){
        component.getStyle().setUnderlined(value);

        return this;
    }

    public ComponentBuilder insertion(String value){
        component.getStyle().setInsertion(value);

        return this;
    }

    public ComponentBuilder obfuscated(boolean value){
        component.getStyle().setObfuscated(value);

        return this;
    }

    public ComponentBuilder shotTip(TextFormatting color, String value) {
        return hover(HoverEvent.Action.SHOW_TEXT, ComponentBuilder.builder(value).color(color).build());
    }

    public ComponentBuilder shotTip(ITextComponent value) {
        return hover(HoverEvent.Action.SHOW_TEXT, value);
    }

    public ComponentBuilder shotTip(String value) {
        return shotTip(TextFormatting.WHITE, value);
    }

    public ComponentBuilder append(ComponentBuilder builder){
        component.appendSibling(builder.build());

        return this;
    }

    public ComponentBuilder append(ITextComponent textComponent){
        component.appendSibling(textComponent);

        return this;
    }

    public ComponentBuilder append(String string){
        component.appendText(string);

        return this;
    }

    public ComponentBuilder append(TextFormatting color, String string){
        component.appendSibling(ComponentBuilder.builder(string).color(color).build());

        return this;
    }

    public ComponentBuilder child(String value){
        return new ComponentBuilder(value, this);
    }

    public ComponentBuilder child(TextFormatting color, String value){
        ComponentBuilder builder = new ComponentBuilder(value, this);
        builder.color(color);

        return builder;
    }

    public ComponentBuilder parent(){
        parent.append(this);

        return parent;
    }

    public ITextComponent build(){
        return component;
    }
}
