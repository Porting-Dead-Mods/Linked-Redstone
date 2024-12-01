package com.portingdeadmods.linkedredstone.datagen;

import com.portingdeadmods.linkedredstone.LinkedRedstone;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockModelProvider extends net.minecraftforge.client.model.generators.BlockModelProvider {

    public BlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, LinkedRedstone.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Still gotta make the textures xd
    }
}
