package com.portingdeadmods.linkedredstone.mixins;

import com.portingdeadmods.linkedredstone.utils.LRUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignalGetter.class)
public abstract class SignalGetterMixin {
    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    private void giveSignal(BlockPos pPos, Direction pDirection, CallbackInfoReturnable<Integer> cir) {
        if (LRUtil.hasPair(pPos, (Level) (Object) this)) {
            cir.setReturnValue(15);
        }
    }
}
