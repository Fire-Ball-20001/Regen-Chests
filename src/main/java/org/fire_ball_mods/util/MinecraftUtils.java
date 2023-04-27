package org.fire_ball_mods.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class MinecraftUtils {

    public static TileEntity getViewBlock(EntityPlayerMP player) {
        RayTraceResult result = rayTrace(player, 5,1);
        if(result == null) {
            return null;
        }
        if(result.typeOfHit != RayTraceResult.Type.BLOCK) {
            return null;
        }
        return player.getEntityWorld().getTileEntity(result.getBlockPos());
    }

    private static RayTraceResult rayTrace(EntityPlayerMP player, double blockReachDistance, float partialTicks)
    {
        Vec3d vec3d = player.getPositionEyes(partialTicks);
        Vec3d vec3d1 = player.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return player.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }
}
