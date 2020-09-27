package com.igteam.immersivegeology.common.world.layer.wld;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.world.biome.IGBiomes;
import com.igteam.immersivegeology.common.world.layer.BiomeLayerData;

import java.util.ArrayList;
import java.util.Arrays;

public class WorldLayerData
{

	public final static WorldLayerData instance = new WorldLayerData();
	
	public ArrayList<BiomeLayerData> worldLayerData = new ArrayList<>();

	public IGBaseBlock LIMESTONE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material);
	public IGBaseBlock MARBLE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material);
	public IGBaseBlock RHYOLITE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material);
	public IGBaseBlock BASALT = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material);
	public IGBaseBlock GRANITE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material);
	public IGBaseBlock PEGMATITE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Pegmatite.material);
	public IGBaseBlock GABBROS = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Gabbros.material);
	
	public IGBaseBlock KAOLINITE = IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Kaolinite.material); //this is getting ore not kaolinite
	
	public WorldLayerData()
	{
		initialize();
	}
	
	public void initialize() {
		buildMountainData();
		buildOceanData();
		buildLowBiomeData();
		buildMediumBiomeData();
	}
	
	private void buildMediumBiomeData() {
		worldLayerData.addAll(Arrays.asList(BiomeLayerBuilder.create(IGBiomes.BADLANDS).addLayerData(GRANITE, GRANITE, GABBROS, GABBROS, BASALT, BASALT)
		.addLayerOreData(4, EnumMaterials.Gold, 0.45f,
							EnumMaterials.Hematite, 0.45f)
		.addLayerOreData(3, EnumMaterials.Gold, 0.5f,
							EnumMaterials.Hematite, 0.45f)
		.addLayerOreData(2, EnumMaterials.Uraninite, 0.25f,
						    EnumMaterials.Ilmenite, 0.38f,
						    EnumMaterials.Hubnerite, 0.35f)
		.addLayerOreData(1, EnumMaterials.Cuprite, 0.30f,
							EnumMaterials.Hematite, 0.45f,
							EnumMaterials.Gold, 0.55f).build()));
	}

	private void buildLowBiomeData() {
		worldLayerData.addAll(Arrays.asList(

				//Desert
				BiomeLayerBuilder.create(IGBiomes.DESERT).addLayerData(GRANITE, PEGMATITE, GRANITE, RHYOLITE, BASALT)
				.addLayerOreData(1, EnumMaterials.Gold, 0.32f,
									EnumMaterials.Hematite, 0.38f,
									EnumMaterials.Cuprite, 0.35f,
									EnumMaterials.Uraninite, 0.15f).build(),

				//Plains
				BiomeLayerBuilder.create(IGBiomes.PLAINS).addLayerData(KAOLINITE, KAOLINITE, KAOLINITE, LIMESTONE, BASALT)
						.addLayerOreData(5, EnumMaterials.Gold, 0.22f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f)
						.addLayerOreData(4, EnumMaterials.Gold, 0.22f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f)
						.addLayerOreData(3, EnumMaterials.Gold, 0.22f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f)
						.addLayerOreData(2, EnumMaterials.Gold, 0.22f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f)
						.addLayerOreData(1, EnumMaterials.Gold, 0.22f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f).build()));
	}

	private void buildOceanData() {
		worldLayerData.addAll(Arrays.asList(//Ocean
				BiomeLayerBuilder.create(IGBiomes.OCEAN).addLayerData(LIMESTONE, LIMESTONE, LIMESTONE, GABBROS, GABBROS, BASALT)
				.addLayerOreData(3, EnumMaterials.Galena, 0.55f,
									EnumMaterials.Cobaltite, 0.5f,
									EnumMaterials.Sphalerite, 0.5f,
									EnumMaterials.Vanadinite, 0.5f,
									EnumMaterials.Platinum, 0.4f,
									EnumMaterials.Cuprite, 0.3f,
									EnumMaterials.Gold, 0.4f)
				.addLayerOreData(2, EnumMaterials.Galena, 0.5f,					//some lead
									EnumMaterials.Cobaltite, 0.63f,				//large amount of cobolt
									EnumMaterials.Platinum, 0.35f,				//small amount of native platinum
									EnumMaterials.Vanadinite, 0.44f,			//Vanadium
									EnumMaterials.Gold, 0.3f)
				.addLayerOreData(1, EnumMaterials.Ullmannite, 0.50f, 			//nickle
									EnumMaterials.Cuprite, 0.53f, 		   		//copper
									EnumMaterials.Cobaltite, 0.43f, 			//cobolt
									EnumMaterials.Pyrolusite, 0.43f,			//Manganese
									EnumMaterials.Gold, 0.33f).build(),

				//Ocean Edge
				BiomeLayerBuilder.create(IGBiomes.OCEAN_EDGE).addLayerData(LIMESTONE, LIMESTONE, LIMESTONE, GABBROS, GABBROS, BASALT)
				.addLayerOreData(3, EnumMaterials.Galena, 0.55f,
									EnumMaterials.Cobaltite, 0.55f,
									EnumMaterials.Sphalerite, 0.56f,
									EnumMaterials.Vanadinite, 0.44f,
									EnumMaterials.Platinum, 0.27f,
									EnumMaterials.Cuprite, 0.43f,
									EnumMaterials.Gold, 0.3f)
				.addLayerOreData(2, EnumMaterials.Galena, 0.55f,					//some lead
									EnumMaterials.Cobaltite, 0.55f,				//large amount of cobolt
									EnumMaterials.Platinum, 0.27f,				//small amount of native platinum
									EnumMaterials.Vanadinite, 0.44f,
									EnumMaterials.Gold, 0.3f)					//Vanadium
				.addLayerOreData(1, EnumMaterials.Ullmannite, 0.46f, 			//nickle
									EnumMaterials.Cuprite, 0.33f, 		   		//copper
									EnumMaterials.Cobaltite, 0.5f,
									EnumMaterials.Platinum, 0.3f,    			//platinum
									EnumMaterials.Pyrolusite, 0.53f,			//Manganese
									EnumMaterials.Gold, 0.33f).build(),

				//Deep Ocean
				BiomeLayerBuilder.create(IGBiomes.DEEP_OCEAN).addLayerData(LIMESTONE, GABBROS, GABBROS, GABBROS, BASALT)
				.addLayerOreData(2, EnumMaterials.Galena, 0.4f,					//some lead
									EnumMaterials.Cobaltite, 0.53f,				//large amount of cobolt
									EnumMaterials.Platinum, 0.24f,				//small amount of native platinum
									EnumMaterials.Vanadinite, 0.54f,			//small-moderate amount of Vanadium
									EnumMaterials.Gold, 0.1f)
				.addLayerOreData(1, EnumMaterials.Ullmannite, 0.50f, 			//nickle
									EnumMaterials.Cuprite, 0.33f, 		   		//copper
									EnumMaterials.Cobaltite, 0.43f, 			//cobolt
									EnumMaterials.Pyrolusite, 0.53f,			//Manganese
									EnumMaterials.Gold, 0.34f).build(),            //decent amount of gold

				//Deep Volcanic Ocean
				//We know that this is going to be a low biome, only need to tell it to generate two layers,
				//we cut it in half remove some work for the ore generator
				BiomeLayerBuilder.create(IGBiomes.DEEP_OCEAN_VOLCANIC).addLayerData(BASALT,BASALT)
				.addLayerOreData(1, EnumMaterials.Gold, 0.3f,
									EnumMaterials.Silver, 0.32f,
									EnumMaterials.Galena, 0.28f,
									EnumMaterials.Cuprite, 0.25f,
									EnumMaterials.Ullmannite, 0.2f,
									EnumMaterials.Sphalerite, 0.25f).build()));
	}

	public void buildMountainData() {
		worldLayerData.addAll(Arrays.asList(//Mountains
				BiomeLayerBuilder.create(IGBiomes.MOUNTAINS).addLayerData(LIMESTONE, LIMESTONE, MARBLE, RHYOLITE, BASALT)
						.addLayerOreData(5, EnumMaterials.Pyrite, 0.62f)
						.addLayerOreData(4, EnumMaterials.Pyrite, 0.42f,
								EnumMaterials.Wolframite, 0.3f)
						.addLayerOreData(3, EnumMaterials.Casserite, 0.32f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f,
								EnumMaterials.Wolframite, 0.25f)
						.addLayerOreData(2, EnumMaterials.Casserite, 0.32f,
								EnumMaterials.Hematite, 0.38f,
								EnumMaterials.Cuprite, 0.35f,
								EnumMaterials.Wolframite, 0.15f)
						.addLayerOreData(1, EnumMaterials.Gold, 0.32f,
									EnumMaterials.Hematite, 0.38f,
									EnumMaterials.Cuprite, 0.35f,
									EnumMaterials.Uraninite, 0.15f).build(),

				//Lush Mountains
				BiomeLayerBuilder.create(IGBiomes.LUSH_MOUNTAINS).addLayerData(LIMESTONE, MARBLE, MARBLE, RHYOLITE, BASALT)
				.addLayerOreData(5,
								EnumMaterials.Magnetite, 0.1f,
								EnumMaterials.Pyrolusite, 0.1f)
				.addLayerOreData(4, EnumMaterials.Sphalerite, 0.6f,
								EnumMaterials.Magnetite, 0.32f,
								EnumMaterials.Pyrolusite, 0.42f)
				.addLayerOreData(3, EnumMaterials.Sphalerite, 0.4f,
								EnumMaterials.Magnetite, 0.3f,
								EnumMaterials.Pyrolusite, 0.4f,
								EnumMaterials.Chromite, 0.2f)
				.addLayerOreData(2, EnumMaterials.Sphalerite, 0.1f,
						EnumMaterials.Magnetite, 0.4f,
						EnumMaterials.Pyrolusite, 0.2f,
						EnumMaterials.Chromite, 0.4f)
				.addLayerOreData(1, EnumMaterials.Sphalerite, 0.32f,
									EnumMaterials.Magnetite, 0.2f,
									EnumMaterials.Pyrolusite, 0.3f,
									EnumMaterials.Chromite, 0.5f).build(),

				//Flooded Mountains
				BiomeLayerBuilder.create(IGBiomes.FLOODED_MOUNTAINS).addLayerData(RHYOLITE, RHYOLITE, GRANITE, MARBLE, BASALT)
						.addLayerOreData(2, EnumMaterials.Gold, 0.4f,
								EnumMaterials.Hematite, 0.3f,
								EnumMaterials.Cuprite, 0.3f)
						.addLayerOreData(2, EnumMaterials.Gold, 0.45f,
								EnumMaterials.Hematite, 0.22f,
								EnumMaterials.Cuprite, 0.32f,
								EnumMaterials.Uraninite, 0.05f)
						.addLayerOreData(3, EnumMaterials.Gold, 0.3f,
								EnumMaterials.Hematite, 0.2f,
								EnumMaterials.Cuprite, 0.3f,
								EnumMaterials.Uraninite, 0.1f)
						.addLayerOreData(2, EnumMaterials.Gold, 0.5f,
								EnumMaterials.Hematite, 0.25f,
								EnumMaterials.Cuprite, 0.25f,
								EnumMaterials.Uraninite, 0.15f)
						.addLayerOreData(1, EnumMaterials.Gold, 0.72f,
								EnumMaterials.Hematite, 0.3f,
								EnumMaterials.Cuprite, 0.2f,
								EnumMaterials.Uraninite, 0.25f)
						.build()));
	}
	
}
