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

    public static final ForgeConfigSpec.BooleanValue FULL_DEBUG = BUILDER
            .comment("Enable full debug logging, this *will* spam your console. This overrides verboseDebug.")
            .define("fullDebug", false);

    public static boolean verboseDebug;
    public static boolean fullDebug;

    static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        verboseDebug = VERBOSE_DEBUG.get();
        fullDebug = FULL_DEBUG.get();
    }
}
