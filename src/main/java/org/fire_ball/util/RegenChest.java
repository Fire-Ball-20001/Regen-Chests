package org.fire_ball.util;

import lombok.AllArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import org.fire_ball.Regen_chests;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class RegenChest {
    public boolean isExample;
    public int countItems;
    public Map<Integer, String> items;

    public List<Map.Entry<Integer,ItemStack>> getItems() {
        List<Map.Entry<Integer, ItemStack>> itemStacks = new ArrayList<>();
        if(isExample) {
            for(Map.Entry<Integer,String> item : items.entrySet()) {
                try {
                    itemStacks.add( new AbstractMap.SimpleEntry<>(item.getKey(),new ItemStack(JsonToNBT.getTagFromJson(item.getValue()))));
                } catch (NBTException e) {
                    Regen_chests.LOG.error("Error load item: "+item.getValue());
                }
            }
        }
        else {
            int count = 0;
            while (count< countItems) {
                for (Map.Entry<Integer, String> item : items.entrySet()) {
                    String key = item.getKey().toString();
                    if (Math.random() * 27 >= Integer.parseInt(key)) continue;
                    if (count >= countItems) break;
                    try {
                        itemStacks.add(new AbstractMap.SimpleEntry<>((int) Math.ceil(Math.random() * 27), new ItemStack(JsonToNBT.getTagFromJson(item.getValue()))));
                        count++;
                    } catch (NBTException e) {
                        Regen_chests.LOG.error("Error load item: " + item.getValue());
                    }
                }
            }
        }
        return itemStacks;
    }
}
