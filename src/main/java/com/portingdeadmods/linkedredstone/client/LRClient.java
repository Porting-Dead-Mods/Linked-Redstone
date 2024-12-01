package com.portingdeadmods.linkedredstone.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = LRClient.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class LRClient {
    public static final String MODID = "linkedredstone";

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }
}

