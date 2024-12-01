package com.portingdeadmods.linkedredstone.tags;

import com.portingdeadmods.linkedredstone.LinkedRedstone;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class LRTags {
    public static final class ItemTags {
        public static final TagKey<Item> LINKING_TOOL = LRTag("linking_tool");
        public static final TagKey<Item> EYE_OF_REDSTONE = LRTag("eye_of_redstone");

        private static TagKey<Item> LRTag(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(LinkedRedstone.MODID, name));
        }
    }

    public static final class BlockTags {
        public static final TagKey<Block> LINKABLE = LRTag("linkable");

        private static TagKey<Block> LRTag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation(LinkedRedstone.MODID, name));
        }
    }
}
