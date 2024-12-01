package com.portingdeadmods.linkedredstone.datagen;

import com.portingdeadmods.linkedredstone.LinkedRedstone;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;



import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = LinkedRedstone.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGatherer {
    private static final String PATH_PREFIX = "textures/block";
    private static final String PATH_SUFFIX = ".png";

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new ItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new EnUsProvider(output));
        generator.addProvider(event.includeClient(), new RecipesProvider(output, lookupProvider));

        BlockTagProvider blockTagProvider = new BlockTagProvider(output, lookupProvider, existingFileHelper);
        ItemTagProvider itemTagProvider = new ItemTagProvider(output, lookupProvider, blockTagProvider.contentsGetter());

        generator.addProvider(event.includeClient(), blockTagProvider);
        generator.addProvider(event.includeClient(), itemTagProvider);
    }
}