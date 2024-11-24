package com.portingdeadmods.linkedredstone.api;

import com.portingdeadmods.linkedredstone.utils.LRUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ILinkable {
    static BlockPos getLinkedBlock(BlockPos pos, Level level) {
        return LRUtil.getPair(pos, level);
    }
}
