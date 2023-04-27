package org.fire_ball_mods.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.util.WeightedRandom;

@Getter
@EqualsAndHashCode
public class NBTWeighted extends WeightedRandom.Item {

    private final String nbt;

    public NBTWeighted(int itemWeightIn, String nbt) {
        super(itemWeightIn);
        this.nbt = nbt;
    }
}
