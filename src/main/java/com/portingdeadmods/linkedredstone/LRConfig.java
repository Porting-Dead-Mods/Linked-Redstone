package com.portingdeadmods.linkedredstone;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = LinkedRedstone.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LRConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue VERBOSE_DEBUG = BUILDER
            .comment("Enable verbose debug logging")
            .define("verboseDebug", false);

    public static boolean verboseDebug;

    static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        verboseDebug = VERBOSE_DEBUG.get();
    }
}
