package com.portingdeadmods.linkedredstone.utils;

import com.portingdeadmods.linkedredstone.LinkedRedstone;
import com.portingdeadmods.linkedredstone.data.LRSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class LRUtil {
    public static BlockPos getPair(BlockPos pos, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return LRSavedData.get(serverLevel).getLinkedBlock(pos);
        } else {
            return null;
        }
        //TODO: Implement networking with client rendering

    }

    public static boolean hasPair(BlockPos pos, Level level) {
        if (pos == null) return false;
        return (getPair(pos, level) != null);
    }

    public static void pair(BlockPos src, BlockPos sb, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            LRSavedData.get(serverLevel).addLinkedPair(src, sb);
        }
    }

    public static void unpair(BlockPos src, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            LRSavedData.get(serverLevel).removeLinkedPair(src);
        }
    }
}
