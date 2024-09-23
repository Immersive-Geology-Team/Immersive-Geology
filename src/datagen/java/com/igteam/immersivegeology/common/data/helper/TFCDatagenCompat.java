/*
 * BluSunrize
 * Copyright (c) 2024
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.helper;

import com.igteam.immersivegeology.common.fluid.IGFluid;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;


public class TFCDatagenCompat
{
	public static void runRecipeDatagen(IGOreBlock oreBlock, Consumer<FinishedRecipe> consumer, RegistryObject<Block> block)
	{
		TFCCollapseRecipeBuilder.builder(Ingredient.of(oreBlock)).copyInput(true).build(consumer, new ResourceLocation(IGLib.MODID, "collapse/" + block.getId().getPath()));
	}

	public static TagKey<Block> getTFCBlockTag(String name)
	{
		try {
			Class<?> clazz = Class.forName("net.dries007.tfc.common.TFCTags$Blocks");
			Field tag_field = clazz.getDeclaredField(name);

			if(Modifier.isStatic(tag_field.getModifiers()) && TagKey.class.isAssignableFrom(tag_field.getType()))
			{
				return (TagKey<Block>) tag_field.get(null);
			}
			else
			{
				IGLib.IG_LOGGER.error("TFC Tried to load a Block Tag, this should not happen");
			}
		} catch(ClassNotFoundException exception)
		{
			IGLib.IG_LOGGER.info("No Class?");
		} catch(NoSuchFieldException exception)
		{

			IGLib.IG_LOGGER.info("No Field?");
		} catch (IllegalAccessException exception)
		{

			IGLib.IG_LOGGER.info("Bad Access?");
		} catch(SecurityException securityException)
		{
			IGLib.IG_LOGGER.info("No Password?");
		}
		return null;
	}

	public static TagKey<Fluid> getTFCFluidTag(String name)
	{
		try {
			Class<?> clazz = Class.forName("net.dries007.tfc.common.TFCTags$Fluids");
			Field tag_field = clazz.getDeclaredField(name);

			if(Modifier.isStatic(tag_field.getModifiers()) && TagKey.class.isAssignableFrom(tag_field.getType()))
			{
				return (TagKey<Fluid>) tag_field.get(null);
			}
			else
			{
				IGLib.IG_LOGGER.error("TFC Tried to load a Fluid Tag, this should not happen");
			}
		} catch(ClassNotFoundException exception)
		{

		} catch(NoSuchFieldException exception)
		{

		} catch (IllegalAccessException exception)
		{

		} catch(SecurityException securityException)
		{

		}
		return null;
	}

	public static TagKey<Item> getTFCItemTag(String name)
	{
		try {
			Class<?> clazz = Class.forName("net.dries007.tfc.common.TFCTags$Items");
			Field tag_field = clazz.getDeclaredField(name);

			if(Modifier.isStatic(tag_field.getModifiers()) && TagKey.class.isAssignableFrom(tag_field.getType()))
			{
				return (TagKey<Item>) tag_field.get(null);
			}
			else
			{
				IGLib.IG_LOGGER.error("TFC Tried to load a Item Tag, this should not happen");
			}
		} catch(ClassNotFoundException exception)
		{

		} catch(NoSuchFieldException exception)
		{

		} catch (IllegalAccessException exception)
		{

		} catch(SecurityException securityException)
		{

		}
		return null;
	}

	public static RecipeSerializer<?> invokeCollapseRecipe()
	{
		try {
			// Step 1: Load the class dynamically
			Class<?> serializersClass = Class.forName("net.dries007.tfc.common.recipes.TFCRecipeSerializers");

			// Step 2: Get the COLLAPSE field
			Field collapseField = serializersClass.getField("COLLAPSE");

			// Step 3: Get the value of the COLLAPSE field (it should be static)
			Object collapseInstance = collapseField.get(null); // Null for static fields

			// Step 4: Get the 'get' method from the COLLAPSE instance
			Method getMethod = collapseInstance.getClass().getMethod("get");

			// Step 5: Invoke the get() method and return the result
			return (RecipeSerializer<?>) getMethod.invoke(collapseInstance);
		} catch (ClassNotFoundException e) {
			System.out.println("Class TFCRecipeSerializers not found: " + e.getMessage());
		} catch (NoSuchFieldException e) {
			System.out.println("Field COLLAPSE does not exist: " + e.getMessage());
		} catch (IllegalAccessException e) {
			System.out.println("Access to field COLLAPSE is denied: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			System.out.println("Method get() does not exist: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}

		return null; // Return null if any issue occurs

	}
}
