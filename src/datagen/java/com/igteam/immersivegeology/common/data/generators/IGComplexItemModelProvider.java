/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators;

import com.igteam.immersivegeology.common.data.TRSRModelBuilder;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGMultiblockProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class IGComplexItemModelProvider extends ModelProvider<TRSRModelBuilder>
{

	public IGComplexItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper)
	{
		super(output, IGLib.MODID, ITEM_FOLDER, TRSRModelBuilder::new, existingFileHelper);
	}

	@Override
	public String getName()
	{
		return getClass().getSimpleName();
	}

	static final ResourceLocation ITEM_GENERATED = new ResourceLocation("minecraft", "item/generated");

	@Override
	protected void registerModels()
	{
		IGLib.IG_LOGGER.info("Started Registration of Immersive Geology Complex Item Models");
		generateCrystallizerModel("crystallizer", IGMultiblockProvider.CRYSTALLIZER.block());
		generateCoredrillMultiblock("coredrill_item", IGMultiblockProvider.COREDRILL.block());
		generateSeparatorModel("gravity_separator", IGMultiblockProvider.GRAVITY_SEPARATOR.block());
		generateRotaryKilnModel("rotary_kiln_item", IGMultiblockProvider.ROTARYKILN.block());
		generateReverberationFurnaceModel("reverberation_furnace", IGMultiblockProvider.REVERBERATION_FURNACE.block());
		generateChemicalReactorModel("chemical_reactor", IGMultiblockProvider.CHEMICAL_REACTOR.block());
		generateBloomeryModel(IGMultiblockProvider.BLOOMERY.block());
		generateGeothermalExchanger(IGMultiblockProvider.GEOTHERMAL_EXCHANGER.block());
		generateSteamTurbine(IGMultiblockProvider.STEAM_TURBINE.block());

		generatePelletizerModel("pelletizer_item", IGMultiblockProvider.PELLETIZER.block());
		generateBallmillModel("display_ballmill", IGMultiblockProvider.BALLMILL.block());
		generateCentrifugeMultiblock("centrifuge", IGMultiblockProvider.CENTRIFUGE.block());
		IGLib.IG_LOGGER.info("Finished Registration of Immersive Geology Complex Item Models");
	}

	private void doTransform(ModelBuilder<?>.TransformsBuilder transform, ItemDisplayContext type, @Nullable Vector3f translation, @Nullable Vector3f rotationAngle, float scale)
	{
		ModelBuilder<?>.TransformsBuilder.TransformVecBuilder trans = transform.transform(type);
		if(translation!=null)
			trans.translation(translation.x(), translation.y(), translation.z());
		if(rotationAngle!=null)
			trans.rotation(rotationAngle.x(), rotationAngle.y(), rotationAngle.z());
		trans.scale(scale);
		trans.end();
	}

	private TRSRModelBuilder obj(Supplier<? extends ItemLike> item, String model)
	{
		return obj(item.get(), model);
	}

	private TRSRModelBuilder obj(ItemLike item, String model)
	{
		return getBuilder(name(item))
				.customLoader(ObjModelBuilder::begin)
				.modelLocation(modLoc("models/"+model)).flipV(true).end();
	}

	private String name(ItemLike item)
	{
		return ForgeRegistries.ITEMS.getKey(item.asItem()).getPath();
	}

	private void generateCentrifugeMultiblock(String id, Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/"+id+"/"+id+".obj");

		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(0f, 2, 0), new Vector3f(30, -45, 0), 0.175F);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
	}

	private void generateCoredrillMultiblock(String id, Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/coredrill/"+id+".obj");

		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(2.5f, 0, 0), new Vector3f(25, -25, 0), 0.09375F);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
	}

	private void generatePelletizerModel(String id, Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/pelletizer/"+id+".obj").texture("pelletizer", new ResourceLocation(IGLib.MODID, "block/multiblock/pelletizer/default"));

		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(1.5f, -2, 0), new Vector3f(30, 45, 0), 0.1875F);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
	}

	private void generateBallmillModel(String id, Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/ballmill/"+id+".obj");

		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(-1, -2f, 0), new Vector3f(30, 45, 0), 0.15F);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(0, -1f, 0), null, 0.125F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
	}

	private void generateSteamTurbine(Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/steam_turbine/steam_turbine.obj").texture("steam_turbine", new ResourceLocation(IGLib.MODID, "block/multiblock/steam_turbine/default"));

		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(-2, 1, 0), new Vector3f(30, 45, 0), 0.16F);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.1875F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, 0, -2), null, 0.0625F);
	}

	private void generateGeothermalExchanger(Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/geothermal_exchanger/geothermal_exchanger.obj").texture("geothermal_exchanger", new ResourceLocation(IGLib.MODID, "block/multiblock/geothermal_exchanger/default"));

		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(-2, 1, 0), new Vector3f(30, 45, 0), 0.16F);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.1875F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, 0, -2), null, 0.0625F);
	}

	private void generateBloomeryModel(Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/bloomery/bloomery.obj").texture("bloomery", new ResourceLocation(IGLib.MODID, "block/multiblock/bloomery/default"));

		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(-4f, -4f, 0), new Vector3f(30, 45, 0), 0.35F);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.1875F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
	}

	private void generateRotaryKilnModel(String id, Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/rotary_kiln/"+id+".obj");
		model.texture("rotary_kiln", "block/multiblock/rotary_kiln/default");
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(2, -1f, 0), new Vector3f(30, 225, 0), 0.14F);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
	}

	private void generateChemicalReactorModel(String id, Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/"+id+"/"+id+".obj");

		model.texture("chemical_reactor", "block/multiblock/chemical_reactor/default");
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(0, -3, 0), new Vector3f(30, 225, 0), 0.095f);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(0, 3, 0), null, 0.0625F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
	}

	private void generateSeparatorModel(String id, Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/"+id+"/"+id+".obj");
		model.texture("gravity_separator", "block/multiblock/gravity_separator/default");
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(0, -5, 0), new Vector3f(30, 225, 0), 0.15625f);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
	}

	private void generateSluiceModel(String id, Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/"+id+"/"+id+".obj");

		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(5, 1, 0), new Vector3f(30, 225, 0), 0.07f);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
	}

	private void generateCrystallizerModel(String id, Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/"+id+"/"+id+".obj");
		model.texture("crystallizer", "block/multiblock/crystallizer/default");
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(0, -2, 0), new Vector3f(30, 225, 0), 0.25f);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(-.5F, 3, -.5F), null, 0.0625F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
	}

	private void generateReverberationFurnaceModel(String id, Supplier<? extends ItemLike> block)
	{
		TRSRModelBuilder model = obj(block, "block/multiblock/obj/"+id+"/"+id+".obj");
		model.texture("reverberation_furnace", "block/multiblock/reverberation_furnace/default");
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, ItemDisplayContext.GUI, new Vector3f(-1, -7, 0), new Vector3f(12, 125, 0), 0.09375f);
		doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
		doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
	}

}