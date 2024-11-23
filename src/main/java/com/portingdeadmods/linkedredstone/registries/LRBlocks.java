package com.portingdeadmods.linkedredstone.registries;

import com.portingdeadmods.linkedredstone.LinkedRedstone;
import com.portingdeadmods.linkedredstone.common.blocks.LinkedObserver;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class LRBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LinkedRedstone.MODID);

    public static final RegistryObject<LinkedObserver> LINKED_OBSERVER = registerBlockAndItem("linked_observer", LinkedObserver::new, BlockBehaviour.Properties.copy(Blocks.OBSERVER), true, true);

    private static <T extends Block> RegistryObject<T> registerBlockAndItem(String name, Function<BlockBehaviour.Properties, T> blockConstructor, BlockBehaviour.Properties properties, boolean addToTab, boolean genItemModel) {
        RegistryObject<T> block = BLOCKS.register(name, () -> blockConstructor.apply(properties));
        RegistryObject<BlockItem> blockItem = LRItems.registerItem(name, props -> new BlockItem(block.get(), props), new Item.Properties(), addToTab);
        if (genItemModel) {
            LRItems.BLOCK_ITEMS.add(blockItem);
        }
        return block;
    }
}
