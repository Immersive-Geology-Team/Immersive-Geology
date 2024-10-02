/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import blusunrize.immersiveengineering.data.blockstates.MultiblockStates;
import com.igteam.immersivegeology.client.renderer.multiblocks.CoreDrillRenderer;
import com.igteam.immersivegeology.common.data.generators.IGDynamicModelProvider.SimpleModelBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.Map.Entry;

public class IGDynamicModelProvider extends ModelProvider<SimpleModelBuilder>
{
	private final IGBlockStateProvider multiblocks;

	public IGDynamicModelProvider(IGBlockStateProvider multiblocks, PackOutput output, ExistingFileHelper existingFileHelper)
	{
		super(output, IGLib.MODID, "dynamic", rl -> new SimpleModelBuilder(rl, existingFileHelper), existingFileHelper);
		this.multiblocks = multiblocks;
	}

	@Override
	protected void registerModels()
	{
		getBuilder(CoreDrillRenderer.DRILL_BIT_NAME)
				.customLoader(ObjModelBuilder::begin)
				.modelLocation(rl("models/block/multiblock/obj/coredrill/coredrill_bit.obj"))
				.flipV(true)
				.end();
		getBuilder(CoreDrillRenderer.DRILL_ENGINE_NAME)
				.customLoader(ObjModelBuilder::begin)
				.modelLocation(rl("models/block/multiblock/obj/coredrill/coredrill_engine.obj"))
				.flipV(true)
				.end();
		getBuilder(CoreDrillRenderer.DRILL_ENGINE_SUPPORT_NAME)
				.customLoader(ObjModelBuilder::begin)
				.modelLocation(rl("models/block/multiblock/obj/coredrill/coredrill_vagons.obj"))
				.flipV(true)
				.end();
		getBuilder(CoreDrillRenderer.DRILL_GEARSET_NAME)
				.customLoader(ObjModelBuilder::begin)
				.modelLocation(rl("models/block/multiblock/obj/coredrill/coredrill_gears.obj"))
				.flipV(true)
				.end();

		for(Entry<Block, ModelFile> multiblock : multiblocks.unsplitModels.entrySet())
			withExistingParent(BuiltInRegistries.BLOCK.getKey(multiblock.getKey()).getPath(), multiblock.getValue().getLocation());
	}

	public static ResourceLocation rl(String path) {
		return new ResourceLocation(IGLib.MODID, path);
	}

	@Nonnull
	@Override
	public String getName()
	{
		return "IG Dynamic models";
	}

	public static class SimpleModelBuilder extends ModelBuilder<SimpleModelBuilder>
	{

		public SimpleModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper)
		{
			super(outputLocation, existingFileHelper);
		}
	}
}
