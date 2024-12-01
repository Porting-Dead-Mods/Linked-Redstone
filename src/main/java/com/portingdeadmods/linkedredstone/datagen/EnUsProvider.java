package com.portingdeadmods.linkedredstone.datagen;

import com.portingdeadmods.linkedredstone.LinkedRedstone;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import static com.portingdeadmods.linkedredstone.registries.LRItems.*;
import static com.portingdeadmods.linkedredstone.registries.LRBlocks.*;

public class EnUsProvider extends LanguageProvider {

    public EnUsProvider(PackOutput output) {
        super(output, LinkedRedstone.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addItem(LINKING_TOOL, "Linking Tool");
        addItem(EYE_OF_REDSTONE, "Eye of Redstone");

        addBlock(LINKED_OBSERVER, "Linked Observer");
    }

    private static String addItem(String name) {
        return "item." + LinkedRedstone.MODID + "." + name;
    }

    private static String addBlock(String name) {
        return "block." + LinkedRedstone.MODID + "." + name;
    }
}
