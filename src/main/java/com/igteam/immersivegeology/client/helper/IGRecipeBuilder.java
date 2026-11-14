/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.client.helper;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.StackWithChance;
import blusunrize.immersiveengineering.api.crafting.TagOutput;
import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class IGRecipeBuilder<R extends IGRecipeBuilder<R>> {
	private final HolderLookup.Provider provider;
	private final RecipeSerializer<?> serializer;
	private final List<Consumer<JsonObject>> writerFunctions;
	private ResourceLocation id;
	protected JsonArray inputArray = null;
	protected int inputCount = 0;
	protected int maxInputCount = 1;
	protected JsonArray resultArray = null;
	protected int resultCount = 0;
	protected int maxResultCount = 1;
	protected final List<ICondition> conditions = new ArrayList<>();

	protected IGRecipeBuilder(RecipeSerializer<?> serializer, HolderLookup.Provider provider) {
		this.provider = provider;
		this.serializer = serializer;
		this.writerFunctions = new ArrayList<>();
	}

	protected boolean isComplete() {
		return true;
	}

	public void build(RecipeOutput out, ResourceLocation id) {
		Preconditions.checkArgument(this.isComplete(), "This recipe is incomplete");
		this.id = id;
		JsonObject json = new JsonObject();
		this.serializeRecipeData(json);
		Recipe<?> recipe = serializer.codec().codec().parse(provider.createSerializationContext(JsonOps.INSTANCE), json).getOrThrow();
		out.accept(id, recipe, null, conditions.toArray(ICondition[]::new));
	}

	public IGRecipeBuilder<R> addWriter(Consumer<JsonObject> writer) {
		Preconditions.checkArgument(this.id == null, "This recipe has already been finalized");
		this.writerFunctions.add(writer);
		return this;
	}

	public IGRecipeBuilder<R> addCondition(ICondition condition) {
		Preconditions.checkArgument(this.id == null, "This recipe has already been finalized");
		this.conditions.add(condition);
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
		return addResult(new IngredientWithSize(ingredient, 1));
	}

	public IGRecipeBuilder<R>  addResult(IngredientWithSize ingredientWithSize) {
		JsonElement result = TagOutput.CODECS.codec().encodeStart(
				provider.createSerializationContext(JsonOps.INSTANCE), new TagOutput(ingredientWithSize)).getOrThrow();
		return this.resultArray != null ? this.addMultiResult(result) : this.addWriter(jsonObject -> jsonObject.add("result", result));
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
		return this.addMultiInput(serializeIngredient(ingredient));
	}

	public IGRecipeBuilder<R>  addMultiInput(IngredientWithSize ingredient) {
		return this.addMultiInput(IngredientWithSize.CODEC.encodeStart(
				provider.createSerializationContext(JsonOps.INSTANCE), ingredient).getOrThrow());
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
		return ItemStack.OPTIONAL_CODEC.encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), stack).getOrThrow().getAsJsonObject();
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
			jsonObject.add(key, serializeIngredient(ingredient));
		});
	}

	public IGRecipeBuilder<R>  addIngredient(String key, IngredientWithSize ingredient) {
		return this.addWriter(jsonObject -> jsonObject.add(key, IngredientWithSize.CODEC.encodeStart(
				provider.createSerializationContext(JsonOps.INSTANCE), ingredient).getOrThrow()));
	}

	public IGRecipeBuilder<R>  addFluid(String key, FluidStack fluidStack) {
		return this.addWriter(jsonObject -> jsonObject.add(key, fluidStack==null?JsonNull.INSTANCE:
				FluidStack.OPTIONAL_CODEC.encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), fluidStack).getOrThrow()));
	}

	public IGRecipeBuilder<R>  addFluid(FluidStack fluidStack) {
		return this.addFluid("fluid", fluidStack);
	}

	public IGRecipeBuilder<R>  addFluid(Fluid fluid, int amount) {
		return this.addFluid("fluid", new FluidStack(fluid, amount));
	}

	public IGRecipeBuilder<R>  addFluidTag(String key, SizedFluidIngredient fluidTag) {
		return this.addWriter(jsonObject -> jsonObject.add(key, SizedFluidIngredient.FLAT_CODEC.encodeStart(
				provider.createSerializationContext(JsonOps.INSTANCE), fluidTag).getOrThrow()));
	}

	public IGRecipeBuilder<R>  addFluidTag(String key, TagKey<Fluid> fluidTag, int amount) {
		return this.addFluidTag(key, SizedFluidIngredient.of(fluidTag, amount));
	}

	public IGRecipeBuilder<R>  addFluidTag(TagKey<Fluid> fluidTag, int amount) {
		return this.addFluidTag("fluid", SizedFluidIngredient.of(fluidTag, amount));
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

	protected JsonObject serializeStackWithChance(IngredientWithSize ingredient, float chance, ICondition... conditions) {
		return StackWithChance.CODECS.codec().encodeStart(provider.createSerializationContext(JsonOps.INSTANCE),
				new StackWithChance(new TagOutput(ingredient), chance, conditions)).getOrThrow().getAsJsonObject();
	}

	private JsonElement serializeIngredient(Ingredient ingredient) {
		return Ingredient.CODEC.encodeStart(provider.createSerializationContext(JsonOps.INSTANCE), ingredient).getOrThrow();
	}
}
