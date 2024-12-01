package com.portingdeadmods.linkedredstone;

import com.mojang.logging.LogUtils;
import com.portingdeadmods.linkedredstone.registries.LRBlocks;
import com.portingdeadmods.linkedredstone.registries.LRItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LinkedRedstone.MODID)
public class LinkedRedstone {
    public static final String MODID = "linkedredstone";
    public static final Logger LRLOGGER = LogUtils.getLogger();

    public LinkedRedstone() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        LRItems.ITEMS.register(modEventBus);
        LRBlocks.BLOCKS.register(modEventBus);
        LRItems.CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LRConfig.SPEC);

        Mixins.addConfiguration("mixins.linkedredstone.json");
    }
}
