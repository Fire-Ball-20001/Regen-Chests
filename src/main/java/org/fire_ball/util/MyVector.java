package org.fire_ball.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class MyVector {
    private int x;
    private int y;
    private int z;

    public MyVector(BlockPos pos) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
    }

    public BlockPos getBlocPos() {
        return new BlockPos(x,y,z);
    }
}
