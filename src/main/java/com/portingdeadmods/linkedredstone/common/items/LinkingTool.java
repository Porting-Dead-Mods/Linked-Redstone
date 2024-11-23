package com.portingdeadmods.linkedredstone.common.items;

import com.portingdeadmods.linkedredstone.utils.LRUtil;
import com.portingdeadmods.linkedredstone.LinkedRedstone;
import com.portingdeadmods.linkedredstone.api.ILinkable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class LinkingTool extends Item {
    public LinkingTool(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (!level.isClientSide) {
            CompoundTag tag = itemstack.getOrCreateTag();
            LinkedRedstone.LRLOGGER.debug("LR - Registered tag for linking tool");
            if (context.getPlayer().isCrouching()) {
                LinkedRedstone.LRLOGGER.debug("LR - Got into crouching block");
                if (level.getBlockState(pos).getBlock() instanceof ILinkable) {
                    LRUtil.readdLong(tag, "selectedRedstoneComponent", pos.asLong());
                    LinkedRedstone.LRLOGGER.debug("LR - Selected redstone component");
                } else {
                    if (player != null) player.sendSystemMessage(Component.nullToEmpty("Block is not linkable"));
                    LinkedRedstone.LRLOGGER.debug("LR - Block is not linkable");
                }
            } else {
                LinkedRedstone.LRLOGGER.debug("LR - Got into NOT-crouching block");
                LRUtil.readdLong(tag, "selectedBlock", pos.asLong());
                LinkedRedstone.LRLOGGER.debug("LR - Selected block");
            }
            itemstack.setTag(tag);
            LinkedRedstone.LRLOGGER.debug("LR - Set tag for linking tool");

            if (itemstack.getTag() != null) {
                LinkedRedstone.LRLOGGER.debug("LR - Tag is registered as not null");
                if (itemstack.getTag().contains("selectedBlock") && itemstack.getTag().contains("selectedRedstoneComponent")) {
                    // ^ Already null proofing the selectedBlock and selectedRedstoneComponent
                    LinkedRedstone.LRLOGGER.debug("LR - Got into linking logic");

                    BlockPos selectedBlock = getSelectedBlock(itemstack);
                    BlockPos selectedRedstoneComponent = getSelectedRedstoneComponent(itemstack);
                    level.getBlockState(selectedRedstoneComponent).getBlock().load(itemstack.getTag());
                    LinkedRedstone.LRLOGGER.debug("LR - Linked 2 blocks");
                }
            }
        }
        LinkedRedstone.LRLOGGER.debug("LR - Interaction ended with success");
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        BlockPos sb = getSelectedBlock(stack);
        BlockPos src = getSelectedRedstoneComponent(stack);

        if (sb != null) {
            components.add(Component.nullToEmpty("Selected block: " + sb.getX() + ", " + sb.getY() + ", " + sb.getZ() + " - " + level.getBlockState(sb).getBlock().getName().getString()));
        }
        if (src != null) {
            components.add(Component.nullToEmpty("Selected redstoneComponent: " + src.getX() + ", " + src.getY() + ", " + src.getZ() + " - " + level.getBlockState(src).getBlock().getName().getString()));
        }
    }

    public static BlockPos getSelectedBlock(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("selectedBlock")) {
            return BlockPos.of(tag.getLong("selectedBlock"));
        }
        return null;
    }

    public static BlockPos getSelectedRedstoneComponent(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("selectedBlock")) {
            return BlockPos.of(tag.getLong("selectedRedstoneComponent"));
        }
        return null;
    }
}
