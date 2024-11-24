package com.portingdeadmods.linkedredstone.common.items;

import com.portingdeadmods.linkedredstone.utils.LRUtil;
import com.portingdeadmods.linkedredstone.api.ILinkable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
            if (level.getBlockState(pos).getBlock() instanceof ILinkable) {
                tag.putLong("selectedRedstoneComponent", pos.asLong());
                if (player != null) player.sendSystemMessage(
                        Component.nullToEmpty("Selected redstone component: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + " - " + level.getBlockState(pos).getBlock().getName().getString())
                );
            } else {
                tag.putLong("selectedBlock", pos.asLong());
                if (player != null) player.sendSystemMessage(
                        Component.nullToEmpty("Selected block: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + " - " + level.getBlockState(pos).getBlock().getName().getString())
                );
            }

            itemstack.setTag(tag);

            if (tag.contains("selectedBlock") && tag.contains("selectedRedstoneComponent")) {
                BlockPos sb = BlockPos.of(tag.getLong("selectedBlock"));
                BlockPos src = BlockPos.of(tag.getLong("selectedRedstoneComponent"));

                LRUtil.pair(src, sb, level);
                if (player != null) player.sendSystemMessage(
                        Component.nullToEmpty("Linked " + sb.getX() + ", " + sb.getY() + ", " + sb.getZ() + " to " + src.getX() + ", " + src.getY() + ", " + src.getZ())
                );

                itemstack.removeTagKey("selectedBlock");
                itemstack.removeTagKey("selectedRedstoneComponent");
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        BlockPos sb = getSelectedBlock(stack);
        BlockPos src = getSelectedRedstoneComponent(stack);

        if (src != null) {
            components.add(Component.nullToEmpty("Selected redstoneComponent: " + src.getX() + ", " + src.getY() + ", " + src.getZ() + " (" + level.getBlockState(src).getBlock().getName().getString() + ")"));
        }

        if (sb != null) {
            components.add(Component.nullToEmpty("Selected block: " + sb.getX() + ", " + sb.getY() + ", " + sb.getZ() + " (" + level.getBlockState(sb).getBlock().getName().getString() + ")"));
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
