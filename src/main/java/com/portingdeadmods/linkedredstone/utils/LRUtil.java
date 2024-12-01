package com.portingdeadmods.linkedredstone.utils;

import com.portingdeadmods.linkedredstone.LinkedRedstone;
import com.portingdeadmods.linkedredstone.data.LRSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;

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

    // Pretty sad that this is the only way I could actually implement true wireless redstone :(
    // Should update this for any mod that uses redstone power under some other property name
    // But PRs exist so oh well
    public static void powerAndUpdate(BlockPos pos, ServerLevel level) {
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(BlockStateProperties.POWER)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWER, 15), 2);
        } else if (state.hasProperty(BlockStateProperties.LIT)) { // Redstone lamp
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, true), 2);
        } else if (state.hasProperty(BlockStateProperties.POWERED)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, true), 2);
        } else if (state.hasProperty(BlockStateProperties.EXTENDED)) { // Piston
            level.setBlock(pos, state.setValue(BlockStateProperties.EXTENDED, true), 2);
        } else if (state.hasProperty(BlockStateProperties.LOCKED)) { // Repeater
            level.setBlock(pos, state.setValue(BlockStateProperties.LOCKED, true), 2);
        } else if (state.hasProperty(BlockStateProperties.ENABLED)) { // Comparator
            level.setBlock(pos, state.setValue(BlockStateProperties.ENABLED, true), 2);
        } else if (state.hasProperty(BlockStateProperties.OPEN)) { // Door
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, true), 2);
        }

        // 2 tick delay (redstone tick)
        level.scheduleTick(pos, state.getBlock(), 2);
    }
}
