package com.portingdeadmods.linkedredstone;

import com.mojang.logging.LogUtils;
import com.portingdeadmods.linkedredstone.registries.LRBlocks;
import com.portingdeadmods.linkedredstone.registries.LRItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

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

        // Mixins.addConfiguration("linkedredstone.mixins.json");
    }
}
