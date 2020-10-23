package com.igteam.immersivegeology.common.data;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialCrystalStructure;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialCrystalBase;
import com.igteam.immersivegeology.common.IGContent;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.blocks.IGCaveBlock;
import com.igteam.immersivegeology.common.blocks.IGLayerBase;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.blocks.crystal.IGGeodeBlock;
import com.igteam.immersivegeology.common.blocks.plant.IGLogBlock;
import com.igteam.immersivegeology.common.blocks.property.IGProperties;
import com.igteam.immersivegeology.common.blocks.property.SpikePart;
import com.igteam.immersivegeology.common.util.IGLogger;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;

/**
 * @author Pabilo8
 * <p>
 * author of the json batch creation system
 * @author Muddykat
 * @since 15.07.2020
 */
public class IGBlockStateProvider extends BlockStateProvider
{
	public IGBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper)
	{
		super(gen, ImmersiveGeology.MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels()
	{
		for(IGBaseBlock block : IGContent.registeredIGBlocks.values())
		{
			try
			{
				if(block instanceof IGGeodeBlock) {
					IGGeodeBlock b = (IGGeodeBlock)block;
					StringBuilder specialName = new StringBuilder();
					String structure = "";
					for(Material material : b.materials)
					{
						if(material.getSpecialSubtypeModelName(b.subtype)!=null)
							specialName.append('_').append(material.getSpecialSubtypeModelName(b.subtype));

						structure = ((MaterialCrystalBase) material).getLatticeStructure().name();
					}

					BlockModelBuilder baseModel = withExistingParent(new ResourceLocation(ImmersiveGeology.MODID, "block/"+block.name + "_" + structure.toString()).getPath(),
							new ResourceLocation(ImmersiveGeology.MODID, "block/base/"+((IGMaterialBlock)block).subtype.getModelPath()+((IGMaterialBlock)block).subtype.getName()+specialName.toString()))
							.texture("base","block/greyscale/crystal/" + structure.toString());

					getVariantBuilder(b).forAllStates(blockState -> ConfiguredModel.builder().modelFile(baseModel).build());

				} else
				if(block instanceof IGCaveBlock) {

					IGCaveBlock b = (IGCaveBlock)block;

					StringBuilder specialName = new StringBuilder();
					for(Material material : b.materials)
					{
						if(material.getSpecialSubtypeModelName(b.subtype)!=null)
							specialName.append('_').append(material.getSpecialSubtypeModelName(b.subtype));
					}

					getVariantBuilder(block).forAllStates(blockState -> {
						Direction facing = blockState.get(IGCaveBlock.FACING);
						SpikePart part = blockState.get(IGCaveBlock.PART);

						BlockModelBuilder baseModel = withExistingParent(new ResourceLocation(ImmersiveGeology.MODID, "block/" + block.name + "_" + part.getName()).getPath(),
								new ResourceLocation(ImmersiveGeology.MODID, "block/base/" + ((IGMaterialBlock) block).subtype.getModelPath(true) + "cave_" + ((IGMaterialBlock) block).subtype.getName() + specialName.toString() + "_" + part.getName()));

						return facing == Direction.DOWN ? ConfiguredModel.builder().modelFile(baseModel).rotationX(180).build() : ConfiguredModel.builder().modelFile(baseModel).build();
					});
				} else
				if(block instanceof IGLogBlock) {

					IGLogBlock b = (IGLogBlock) block;

					StringBuilder specialName = new StringBuilder();
					for (Material material : b.materials) {
						if (material.getSpecialSubtypeModelName(b.subtype) != null)
							specialName.append('_').append(material.getSpecialSubtypeModelName(b.subtype));
					}
					BlockModelBuilder baseModel = withExistingParent(new ResourceLocation(ImmersiveGeology.MODID, "block/" + block.name).getPath(),
							new ResourceLocation(ImmersiveGeology.MODID, "block/base/" + ((IGMaterialBlock) block).subtype.getModelPath(false) + specialName.toString()))
							.texture("side", new ResourceLocation(ImmersiveGeology.MODID, "block/plant/logs/" + b.getMaterial().getName() + "/" + b.getMaterial().getName() + "_" + ((IGLogBlock) block).getUseType().getName() + "_side"))
							.texture("end", new ResourceLocation(ImmersiveGeology.MODID, "block/plant/logs/" + b.getMaterial().getName() + "/" + b.getMaterial().getName() + "_" + ((IGLogBlock) block).getUseType().getName() + "_top"));

					getVariantBuilder(block).forAllStates(blockState -> blockState.get(IGLogBlock.AXIS) == Direction.Axis.Z ? ConfiguredModel.builder().modelFile(baseModel).rotationX(90).build() : blockState.get(IGLogBlock.AXIS) == Direction.Axis.X ? ConfiguredModel.builder().modelFile(baseModel).rotationX(90).rotationY(90).build() : ConfiguredModel.builder().modelFile(baseModel).build());

				} else if(block instanceof IGLayerBase) {
					IGLayerBase b = (IGLayerBase)block;

					StringBuilder specialName = new StringBuilder();
					for(Material material : b.materials)
					{
						if(material.getSpecialSubtypeModelName(b.subtype)!=null)
							specialName.append('_').append(material.getSpecialSubtypeModelName(b.subtype));
					}
					getVariantBuilder(block).forAllStates(blockState -> {
						int layer_state = blockState.get(IGLayerBase.LAYERS);

						String layer_name = layer_state == 8 ? "" : "_height" + layer_state * 2;

						BlockModelBuilder varientModel = withExistingParent(new ResourceLocation(ImmersiveGeology.MODID, "block/" + b.name).getPath() + layer_name,
								new ResourceLocation(ImmersiveGeology.MODID, "block/base/layer/" + (b).subtype.getName() + specialName.toString() + layer_name))
								.texture("base", "block/greyscale/layer/" + b.getMaterial().getName());

						return ConfiguredModel.builder().modelFile(varientModel).build();
					});
				}
				else if(block instanceof IGMaterialBlock) {
					IGMaterialBlock b = (IGMaterialBlock) block;

					StringBuilder specialName = new StringBuilder();
					for (Material material : b.materials) {
						if (material.getSpecialSubtypeModelName(b.subtype) != null)
							specialName.append('_').append(material.getSpecialSubtypeModelName(b.subtype));
					}
					BlockModelBuilder baseModel = withExistingParent(new ResourceLocation(ImmersiveGeology.MODID, "block/" + block.name).getPath(),
							new ResourceLocation(ImmersiveGeology.MODID, "block/base/" + ((IGMaterialBlock) block).subtype.getModelPath(false) + specialName.toString()));


					if (b.hasMultipartModel()) {
						//An advanced model, with each property having an attached model
						MultiPartBlockStateBuilder multipart = getMultipartBuilder(block);
						multipart.part().modelFile(baseModel).addModel();
						String res = new ResourceLocation(ImmersiveGeology.MODID, "block/base/" + ((IGMaterialBlock) block).subtype.getModelPath(true)).toString();
						String subName = specialName.substring(1);

						//Insert new properties below, the system is very extendable

						//Richness property, get models for currentname_tier for all richness types
						if (b.getDefaultState().getProperties().contains(IGProperties.ORE_RICHNESS)) {
							//For now, a static texture, poke @Pabilo8 if you need a texture for every block type possible
							BlockModelBuilder poorModel = withExistingParent(res + "poor", res + "richness").texture("base",
									new ResourceLocation(ImmersiveGeology.MODID, "block/greyscale/rock/ore_bearing/" + subName + "/" + subName + "_poor"));

							BlockModelBuilder normalModel = withExistingParent(res + "normal", res + "richness").texture("base",
									new ResourceLocation(ImmersiveGeology.MODID, "block/greyscale/rock/ore_bearing/" + subName + "/" + subName + "_normal"));

							BlockModelBuilder richModel = withExistingParent(res + "rich", res + "richness").texture("base",
									new ResourceLocation(ImmersiveGeology.MODID, "block/greyscale/rock/ore_bearing/" + subName + "/" + subName + "_rich"));

							BlockModelBuilder denseModel = withExistingParent(res + "dense", res + "richness").texture("base",
									new ResourceLocation(ImmersiveGeology.MODID, "block/greyscale/rock/ore_bearing/" + subName + "/" + subName + "_dense"));

							multipart.part().modelFile(poorModel).addModel().condition(IGProperties.ORE_RICHNESS, 0);
							multipart.part().modelFile(normalModel).addModel().condition(IGProperties.ORE_RICHNESS, 1);
							multipart.part().modelFile(richModel).addModel().condition(IGProperties.ORE_RICHNESS, 2);
							multipart.part().modelFile(denseModel).addModel().condition(IGProperties.ORE_RICHNESS, 3);
						}
					}
					else
					{
						//A basic BlockState using a single model
						getVariantBuilder(block).forAllStates(blockState -> ConfiguredModel.builder().modelFile(baseModel).build());
					}
				}
			} catch(Exception e)
			{
				IGLogger.info("Failed to create blockstate: "+e);
			}

		}
	}

	@Override
	public String getName()
	{
		return "IG BlockState Provider";
	}
}
