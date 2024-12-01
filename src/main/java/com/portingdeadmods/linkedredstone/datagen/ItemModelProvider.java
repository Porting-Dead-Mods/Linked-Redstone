package com.portingdeadmods.linkedredstone.datagen;


import com.portingdeadmods.linkedredstone.LinkedRedstone;
import com.portingdeadmods.linkedredstone.registries.LRItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider {
    public ItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, LinkedRedstone.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(LRItems.LINKING_TOOL.get());
        basicItem(LRItems.EYE_OF_REDSTONE.get());
    }
}
