package com.nosiphus.furniture.core;

import com.mrcrayfish.furniture.item.crafting.SimpleCookingSerializer;
import com.nosiphus.furniture.NosiphusFurnitureMod;
import com.nosiphus.furniture.recipe.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializer {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, NosiphusFurnitureMod.MOD_ID);

    public static final RegistryObject<SimpleCookingSerializer<ChoppingRecipe>> CHOPPING = RECIPE_SERIALIZER.register("chopping",
            () -> new SimpleCookingSerializer<>(ChoppingRecipe::new, 0));
    public static final RegistryObject<RecipeSerializer<CookingRecipe>> COOKING = RECIPE_SERIALIZER.register("cooking",
            () -> CookingRecipe.Serializer.INSTANCE);

}
