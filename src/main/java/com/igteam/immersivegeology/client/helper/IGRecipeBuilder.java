/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.helper;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class IGRecipeBuilder<R extends IGRecipeBuilder<R>> implements FinishedRecipe {
	private final RecipeSerializer<?> serializer;
	private final List<Consumer<JsonObject>> writerFunctions;
	private ResourceLocation id;
	protected JsonArray inputArray = null;
	protected int inputCount = 0;
	protected int maxInputCount = 1;
	protected JsonArray resultArray = null;
	protected int resultCount = 0;
	protected int maxResultCount = 1;
	protected JsonArray conditions = null;

	protected IGRecipeBuilder(RecipeSerializer<?> serializer) {
		this.serializer = serializer;
		this.writerFunctions = new ArrayList<>();
	}

	protected boolean isComplete() {
		return true;
	}

	public void build(Consumer<FinishedRecipe> out, ResourceLocation id) {
		Preconditions.checkArgument(this.isComplete(), "This recipe is incomplete");
		this.id = id;
		out.accept(this);
	}

	public IGRecipeBuilder<R> addWriter(Consumer<JsonObject> writer) {
		Preconditions.checkArgument(this.id == null, "This recipe has already been finalized");
		this.writerFunctions.add(writer);
		return this;
	}

	public IGRecipeBuilder<R> addCondition(ICondition condition) {
		if (this.conditions == null) {
			this.conditions = new JsonArray();
			this.addWriter((jsonObject) -> {
				jsonObject.add("conditions", this.conditions);
			});
		}

		this.conditions.add(CraftingHelper.serialize(condition));
		return this;
	}

	public IGRecipeBuilder<R>  setTime(int time) {
		return this.addWriter((jsonObject) -> {
			jsonObject.addProperty("time", time);
		});
	}

	public IGRecipeBuilder<R>  setEnergy(int energy) {
		return this.addWriter((jsonObject) -> {
			jsonObject.addProperty("energy", energy);
		});
	}

	public IGRecipeBuilder<R>  setMultipleResults(int maxResultCount) {
		this.resultArray = new JsonArray();
		this.maxResultCount = maxResultCount;
		return this.addWriter((jsonObject) -> {
			jsonObject.add("results", this.resultArray);
		});
	}

	public IGRecipeBuilder<R>  addMultiResult(JsonElement obj) {
		Preconditions.checkArgument(this.maxResultCount > 1, "This recipe does not support multiple results");
		Preconditions.checkArgument(this.resultCount < this.maxResultCount, "Recipe can only have " + this.maxResultCount + " results");
		this.resultArray.add(obj);
		++this.resultCount;
		return this;
	}

	public IGRecipeBuilder<R> addResult(ItemLike itemProvider) {
		return this.addResult(new ItemStack(itemProvider));
	}

	public IGRecipeBuilder<R>  addResult(ItemStack itemStack) {
		return this.resultArray != null ? this.addMultiResult(this.serializeItemStack(itemStack)) : this.addItem("result", itemStack);
	}

	public IGRecipeBuilder<R>  addResult(Ingredient ingredient) {
		return this.resultArray != null ? this.addMultiResult(ingredient.toJson()) : this.addWriter((jsonObject) -> {
			jsonObject.add("result", ingredient.toJson());
		});
	}

	public IGRecipeBuilder<R>  addResult(IngredientWithSize ingredientWithSize) {
		return this.resultArray != null ? this.addMultiResult(ingredientWithSize.serialize()) : this.addWriter((jsonObject) -> {
			jsonObject.add("result", ingredientWithSize.serialize());
		});
	}

	public IGRecipeBuilder<R>  setUseInputArray(int maxInputCount, String key) {
		this.inputArray = new JsonArray();
		this.maxInputCount = maxInputCount;
		return this.addWriter((jsonObject) -> {
			jsonObject.add(key, this.inputArray);
		});
	}

	public IGRecipeBuilder<R>  setUseInputArray(int maxInputCount) {
		return this.setUseInputArray(maxInputCount, "inputs");
	}

	public IGRecipeBuilder<R>  addMultiInput(JsonElement obj) {
		Preconditions.checkArgument(this.maxInputCount > 1, "This recipe does not support multiple inputs");
		Preconditions.checkArgument(this.inputCount < this.maxInputCount, "Recipe can only have " + this.maxInputCount + " inputs");
		this.inputArray.add(obj);
		++this.inputCount;
		return this;
	}

	public IGRecipeBuilder<R>  addMultiInput(Ingredient ingredient) {
		return this.addMultiInput(ingredient.toJson());
	}

	public IGRecipeBuilder<R>  addMultiInput(IngredientWithSize ingredient) {
		return this.addMultiInput(ingredient.serialize());
	}

	protected String generateSafeInputKey() {
		Preconditions.checkArgument(this.inputCount < this.maxInputCount, "Recipe can only have " + this.maxInputCount + " inputs");
		String key = this.maxInputCount == 1 ? "input" : "input" + this.inputCount;
		++this.inputCount;
		return key;
	}

	public IGRecipeBuilder<R>  addInput(ItemLike... itemProviders) {
		return this.inputArray != null ? this.addMultiInput(Ingredient.of(itemProviders)) : this.addIngredient(this.generateSafeInputKey(), itemProviders);
	}

	public IGRecipeBuilder<R>  addInput(ItemStack... itemStacks) {
		return this.inputArray != null ? this.addMultiInput(Ingredient.of(itemStacks)) : this.addIngredient(this.generateSafeInputKey(), itemStacks);
	}

	public IGRecipeBuilder<R>  addInput(TagKey<Item> tag) {
		return this.inputArray != null ? this.addMultiInput(Ingredient.of(tag)) : this.addIngredient(this.generateSafeInputKey(), tag);
	}

	public IGRecipeBuilder<R>  addInput(Ingredient input) {
		return this.inputArray != null ? this.addMultiInput(input) : this.addIngredient(this.generateSafeInputKey(), input);
	}

	public IGRecipeBuilder<R>  addInput(IngredientWithSize input) {
		return this.inputArray != null ? this.addMultiInput(input) : this.addIngredient(this.generateSafeInputKey(), input);
	}

	public JsonObject serializeItemStack(ItemStack stack) {
		JsonObject obj = new JsonObject();
		obj.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
		if (stack.getCount() > 1) {
			obj.addProperty("count", stack.getCount());
		}

		if (stack.hasTag()) {
			obj.addProperty("nbt", stack.getTag().toString());
		}

		return obj;
	}

	protected IGRecipeBuilder<R>  addSimpleItem(String key, ItemLike item) {
		return this.addWriter((json) -> {
			json.addProperty(key, BuiltInRegistries.ITEM.getKey(item.asItem()).toString());
		});
	}

	public IGRecipeBuilder<R>  addItem(String key, ItemLike item) {
		return this.addItem(key, new ItemStack(item));
	}

	public IGRecipeBuilder<R>  addItem(String key, ItemStack stack) {
		Preconditions.checkArgument(!stack.isEmpty(), "May not add empty ItemStack to recipe");
		return this.addWriter((jsonObject) -> {
			jsonObject.add(key, this.serializeItemStack(stack));
		});
	}

	public IGRecipeBuilder<R>  addIngredient(String key, ItemLike... itemProviders) {
		return this.addIngredient(key, Ingredient.of(itemProviders));
	}

	public IGRecipeBuilder<R>  addIngredient(String key, ItemStack... itemStacks) {
		return this.addIngredient(key, Ingredient.of(itemStacks));
	}

	public IGRecipeBuilder<R>  addIngredient(String key, TagKey<Item> tag) {
		return this.addIngredient(key, Ingredient.of(tag));
	}

	public IGRecipeBuilder<R>  addIngredient(String key, Ingredient ingredient) {
		return this.addWriter((jsonObject) -> {
			jsonObject.add(key, ingredient.toJson());
		});
	}

	public IGRecipeBuilder<R>  addIngredient(String key, IngredientWithSize ingredient) {
		return this.addWriter((jsonObject) -> {
			jsonObject.add(key, ingredient.serialize());
		});
	}

	public IGRecipeBuilder<R>  addFluid(String key, FluidStack fluidStack) {
		return this.addWriter((jsonObject) -> {
			jsonObject.add(key, ApiUtils.jsonSerializeFluidStack(fluidStack));
		});
	}

	public IGRecipeBuilder<R>  addFluid(FluidStack fluidStack) {
		return this.addFluid("fluid", fluidStack);
	}

	public IGRecipeBuilder<R>  addFluid(Fluid fluid, int amount) {
		return this.addFluid("fluid", new FluidStack(fluid, amount));
	}

	public IGRecipeBuilder<R>  addFluidTag(String key, FluidTagInput fluidTag) {
		return this.addWriter((jsonObject) -> {
			jsonObject.add(key, fluidTag.serialize());
		});
	}

	public IGRecipeBuilder<R>  addFluidTag(String key, TagKey<Fluid> fluidTag, int amount) {
		return this.addFluidTag(key, new FluidTagInput(fluidTag, amount, (CompoundTag)null));
	}

	public IGRecipeBuilder<R>  addFluidTag(TagKey<Fluid> fluidTag, int amount) {
		return this.addFluidTag("fluid", new FluidTagInput(fluidTag, amount, (CompoundTag)null));
	}

	public void serializeRecipeData(JsonObject jsonObject) {
		Iterator var2 = this.writerFunctions.iterator();

		while(var2.hasNext()) {
			Consumer<JsonObject> writer = (Consumer)var2.next();
			writer.accept(jsonObject);
		}

	}

	public ResourceLocation getId() {
		return this.id;
	}

	public RecipeSerializer<?> getType() {
		return this.serializer;
	}

	@Nullable
	public JsonObject serializeAdvancement() {
		return null;
	}

	@Nullable
	public ResourceLocation getAdvancementId() {
		return null;
	}

	protected static JsonObject serializeStackWithChance(IngredientWithSize ingredient, float chance, ICondition... conditions) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("chance", chance);
		jsonObject.add("output", ingredient.serialize());
		if (conditions.length > 0) {
			JsonArray conditionArray = new JsonArray();
			ICondition[] var5 = conditions;
			int var6 = conditions.length;

			for(int var7 = 0; var7 < var6; ++var7) {
				ICondition condition = var5[var7];
				conditionArray.add(CraftingHelper.serialize(condition));
			}

			jsonObject.add("conditions", conditionArray);
		}

		return jsonObject;
	}
}
