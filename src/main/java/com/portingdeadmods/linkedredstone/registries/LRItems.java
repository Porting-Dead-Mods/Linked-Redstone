package com.portingdeadmods.linkedredstone.registries;

import com.portingdeadmods.linkedredstone.LinkedRedstone;
import com.portingdeadmods.linkedredstone.common.items.LinkingTool;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class LRItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LinkedRedstone.MODID);
    public static final List<Supplier<BlockItem>> BLOCK_ITEMS = new ArrayList<>();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LinkedRedstone.MODID);
    public static final List<RegistryObject<?>> CREATIVE_TAB_ITEMS = new ArrayList<>();


    public static final RegistryObject<LinkingTool> LINKING_TOOL = registerItem("linking_tool", () -> new LinkingTool(new Item.Properties()));
    public static final RegistryObject<Item> EYE_OF_REDSTONE = registerItem("eye_of_redstone", () -> new Item(new Item.Properties()));

    public static <T extends Item> RegistryObject<T> registerItem(String name, Function<Item.Properties, T> itemConstructor, Item.Properties properties) {
        return registerItem(name, itemConstructor, properties, true);
    }

    public static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item) {
        RegistryObject<T> toReturn = ITEMS.register(name, item);
        CREATIVE_TAB_ITEMS.add(toReturn);
        return toReturn;
    }

    public static <T extends Item> RegistryObject<T> registerItem(String name, Function<Item.Properties, T> itemConstructor, Item.Properties properties, boolean addToTab) {
        RegistryObject<T> toReturn = ITEMS.register(name, () -> itemConstructor.apply(properties));
        if (addToTab) {
            CREATIVE_TAB_ITEMS.add(toReturn);
        }
        return toReturn;
    }

    public static final RegistryObject<CreativeModeTab> LINKED_REDSTONE_TAB = CREATIVE_MODE_TABS.register("linked_redstone_tab", () -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> EYE_OF_REDSTONE.get().getDefaultInstance()).displayItems((parameters, output) -> {
        for (RegistryObject<?> item : CREATIVE_TAB_ITEMS) {
            output.accept((ItemLike) item.get());
        }
    }).build());
}
