package com.portingdeadmods.linkedredstone.datagen;

import com.portingdeadmods.linkedredstone.LinkedRedstone;
import com.portingdeadmods.linkedredstone.tags.LRTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.portingdeadmods.linkedredstone.registries.LRItems.*;

public class ItemTagProvider extends ItemTagsProvider {

    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(LRTags.ItemTags.EYE_OF_REDSTONE,
                EYE_OF_REDSTONE.get()
        );

        tag(LRTags.ItemTags.LINKING_TOOL,
                LINKING_TOOL.get()
        );
    }

    private void tag(TagKey<Item> itemTagKey, ItemLike... items) {
        IntrinsicTagAppender<Item> tag = tag(itemTagKey);
        for (ItemLike item : items) {
            tag.add(item.asItem());
        }
    }

    @SafeVarargs
    private void tag(TagKey<Item> itemTagKey, TagKey<Item>... items) {
        IntrinsicTagAppender<Item> tag = tag(itemTagKey);
        for (TagKey<Item> item : items) {
            tag.addTag(item);
        }
    }
}
