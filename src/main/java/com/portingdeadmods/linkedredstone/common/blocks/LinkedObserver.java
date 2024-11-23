package com.portingdeadmods.linkedredstone.common.blocks;

import com.portingdeadmods.linkedredstone.api.ILinkable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LinkedObserver extends ObserverBlock implements ILinkable {
    public LinkedObserver(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState blockState, @NotNull ServerLevel level, @NotNull BlockPos pos, RandomSource randomSource) {
        if (level.getBlockEntity(pos) == null) {
            return;
        } // Quick and dirty fix for this

        if ((Boolean) blockState.getValue(POWERED)) {
            level.setBlock(getLinkedBlock(pos, level), (BlockState) blockState.setValue(POWERED, false), 2);
        } else {
            level.setBlock(getLinkedBlock(pos, level), (BlockState) blockState.setValue(POWERED, true), 2);
            level.scheduleTick(getLinkedBlock(pos, level), this, 2);
        }
        this.updateNeighborsInFront(level, pos, blockState);
    }

    public BlockPos getLinkedBlock(BlockPos pos, Level level) {
        CompoundTag tag = level.getBlockEntity(pos).saveWithoutMetadata();
        return BlockPos.of(tag.getLong("selectedBlock"));
    }

    public void setLinkedBlock(BlockPos pos, Level level, CompoundTag tag) {
        level.getBlockEntity(pos).load(tag);
    }


}
