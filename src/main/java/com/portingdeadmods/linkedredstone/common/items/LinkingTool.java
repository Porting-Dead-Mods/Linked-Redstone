package com.portingdeadmods.linkedredstone.common.items;

import com.portingdeadmods.linkedredstone.api.ILinkable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class LinkingTool extends Item {
    public LinkingTool(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (!level.isClientSide) {
            CompoundTag tag = itemstack.getOrCreateTag();
            if (context.getPlayer().isCrouching()) {
                if (level.getBlockEntity(pos) instanceof ILinkable) {
                    tag.putLong("selectedRedstoneComponent", pos.asLong());
                } else {
                    if (player != null) player.sendSystemMessage(Component.nullToEmpty("Block is not linkable"));
                }
            } else {
                tag.putLong("selectedBlock", pos.asLong());
            }
            itemstack.setTag(tag);
        }

        if (itemstack.getTag().contains("selectedBlock") && itemstack.getTag().contains("selectedRedstoneComponent")) {
            BlockPos selectedBlock = getSelectedBlock(itemstack);
            BlockPos selectedRedstoneComponent = getSelectedRedstoneComponent(itemstack);
            ((ILinkable) level.getBlockEntity(selectedRedstoneComponent)).setLinkedBlock(selectedBlock, level, itemstack.getTag());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        BlockPos selectedBlock = getSelectedBlock(stack);
        if (selectedBlock != null) {
            components.add(Component.nullToEmpty("Selected block: " + selectedBlock.getX() + ", " + selectedBlock.getY() + ", " + selectedBlock.getZ()));
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
        if (tag != null && tag.contains("selectedRedstoneComponent")) {
            return BlockPos.of(tag.getLong("selectedBlock"));
        }
        return null;
    }
}
