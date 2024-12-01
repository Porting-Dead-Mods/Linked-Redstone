package com.portingdeadmods.linkedredstone.common.blocks;

import com.portingdeadmods.linkedredstone.LRConfig;
import com.portingdeadmods.linkedredstone.LinkedRedstone;
import com.portingdeadmods.linkedredstone.api.ILinkable;
import com.portingdeadmods.linkedredstone.utils.LRUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
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
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        LinkedRedstone.LRLOGGER.debug("Ticked LinkedObserver at " + pPos.getX() + ", " + pPos.getY() + ", " + pPos.getZ());
        BlockPos sb = getLinkedBlock(pPos, pLevel);
        if (pState.getValue(POWERED)) {
            pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.valueOf(false)), 2);
            if (LRUtil.hasPair(sb, pLevel)) {
                if (LRConfig.verboseDebug || LRConfig.fullDebug) LinkedRedstone.LRLOGGER.debug("LinkedObserver at " + pPos.getX() + ", " + pPos.getY() + ", " + pPos.getZ() + " tried to power " + sb.getX() + ", " + sb.getY() + ", " + sb.getZ());
                pLevel.setBlock(sb, pLevel.getBlockState(sb).setValue(POWERED, Boolean.valueOf(false)), 2);
            } else  {
                if (LRConfig.verboseDebug || LRConfig.fullDebug) LinkedRedstone.LRLOGGER.debug("LinkedObserver at " + pPos.getX() + ", " + pPos.getY() + ", " + pPos.getZ() + " would power but has no linked block");
            }
        } else {
            pLevel.setBlock(pPos, pState.setValue(POWERED, Boolean.valueOf(true)), 2);
            pLevel.scheduleTick(pPos, this, 2);
        }

        this.updateNeighborsInFront(pLevel, pPos, pState);
    }

    @Override
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        BlockPos linkedBlockPos = getLinkedBlock(pPos, (Level) pBlockAccess);
        if (linkedBlockPos != null) {
            BlockState linkedBlockState = pBlockAccess.getBlockState(linkedBlockPos);
            return linkedBlockState.getValue(POWERED) && linkedBlockState.getValue(FACING) == pSide ? 15 : 0;
        }
        return 0;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            if (!pLevel.isClientSide && pState.getValue(POWERED) && pLevel.getBlockTicks().hasScheduledTick(pPos, this)) {
                this.updateNeighborsInFront(pLevel, pPos, pState.setValue(POWERED, Boolean.valueOf(false)));
            }
        }
    }

    public BlockPos getLinkedBlock(BlockPos pos, Level level) {
        return ILinkable.getLinkedBlock(pos, level);
    }
}
