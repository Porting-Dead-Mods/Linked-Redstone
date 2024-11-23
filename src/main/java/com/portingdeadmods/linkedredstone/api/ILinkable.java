package com.portingdeadmods.linkedredstone.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.Objects;

public interface ILinkable {
    static BlockPos getLinkedBlock(BlockPos pos, Level level) {
        CompoundTag tag = level.getBlockEntity(pos).saveWithoutMetadata();
        return BlockPos.of(tag.getLong("selectedBlock"));
    }
    static void setLinkedBlock(BlockPos pos, Level level, CompoundTag tag) {
        level.getBlockEntity(pos).load(tag);
    }
}
