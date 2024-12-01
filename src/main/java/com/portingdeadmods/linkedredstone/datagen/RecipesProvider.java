package com.portingdeadmods.linkedredstone.datagen;

import com.portingdeadmods.linkedredstone.registries.LRItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.portingdeadmods.linkedredstone.registries.LRItems.*;
import static com.portingdeadmods.linkedredstone.registries.LRBlocks.*;

public class RecipesProvider extends RecipeProvider {
    public RecipesProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput);
    }

    private static void linkableComponentRecipe(Consumer<FinishedRecipe> pWriter, ItemLike pResult, ItemLike pIngredient) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, pResult)
                .requires(pIngredient, 1)
                .requires(EYE_OF_REDSTONE.get(), 1)
                .unlockedBy("has_item", has(EYE_OF_REDSTONE.get()))
                .save(pWriter);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, EYE_OF_REDSTONE.get().asItem())
                .requires(Items.ENDER_PEARL, 1)
                .requires(Items.REDSTONE, 1)
                .unlockedBy("has_item", has(Items.ENDER_PEARL))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, LINKING_TOOL.get().asItem())
                .pattern("  E")
                .pattern(" C ")
                .pattern("C  ")
                .define('E', EYE_OF_REDSTONE.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_item", has(EYE_OF_REDSTONE.get()))
                .save(pWriter);

        linkableComponentRecipe(pWriter, LINKED_OBSERVER.get().asItem(), Items.OBSERVER);
    }
}
