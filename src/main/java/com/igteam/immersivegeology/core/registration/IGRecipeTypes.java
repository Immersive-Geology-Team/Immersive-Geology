/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.registration;

import blusunrize.immersiveengineering.api.crafting.IERecipeTypes.TypeWithClass;
import com.igteam.immersivegeology.common.block.multiblocks.recipe.*;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class IGRecipeTypes
{
	private static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, IGLib.MODID);

	public static final TypeWithClass<CoreDrillRecipe> COREDRILL = register("coredrill", CoreDrillRecipe.class);
	public static final TypeWithClass<CrystallizerRecipe> CRYSTALLIZER = register("crystallizer", CrystallizerRecipe.class);
	public static final TypeWithClass<CentrifugeRecipe> CENTRIFUGE = register("centrifuge", CentrifugeRecipe.class);
	public static final TypeWithClass<GravitySeparatorRecipe> GRAVITYSEPARATOR = register("gravityseparator", GravitySeparatorRecipe.class);
	public static final TypeWithClass<IndustrialSluiceRecipe> SLUICE = register("industrial_sluice", IndustrialSluiceRecipe.class);
	public static final TypeWithClass<RevFurnaceRecipe> REVFURNACE = register("reverberation_furnace", RevFurnaceRecipe.class);
	public static final TypeWithClass<RotaryKilnRecipe> ROTARYKILN = register("rotarykiln", RotaryKilnRecipe.class);
	public static final TypeWithClass<ChemicalRecipe> CHEMICAL_REACTOR = register("chemical_reactor", ChemicalRecipe.class);
	public static final TypeWithClass<BloomeryRecipe> BLOOMERY = register("bloomery", BloomeryRecipe.class);

	private static <T extends Recipe<?>>
	TypeWithClass<T> register(String name, Class<T> type)
	{
		RegistryObject<RecipeType<T>> regObj = REGISTER.register(name, () -> new RecipeType<>()
		{
		});
		return new TypeWithClass<>(regObj, type);
	}

	public static void init()
	{
		REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
