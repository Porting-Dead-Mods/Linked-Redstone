package com.portingdeadmods.linkedredstone.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public interface ILinkable {
    BlockPos getLinkedBlock(BlockPos pos, Level level);
    void setLinkedBlock(BlockPos pos, Level level, CompoundTag tag);
}
