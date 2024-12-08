package com.portingdeadmods.linkedredstone.mixins;

import com.portingdeadmods.linkedredstone.utils.LRUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignalGetter.class)
public interface SignalGetterMixin extends SignalGetter {
    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    private void getSignal(BlockPos pPos, Direction pDirection, CallbackInfoReturnable<Integer> cir) {
        if (LRUtil.hasSRC(pPos, (Level) this)) {
            if (LRUtil.isPowered(LRUtil.getSRC(pPos, (Level) this), (Level) this)) {
                cir.setReturnValue(15);
            } else {
                cir.setReturnValue(0);
            }
        }
    }
}
