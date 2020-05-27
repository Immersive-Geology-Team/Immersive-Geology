package com.igteam.immersivegeology.common.world.biome;

import java.util.ArrayList;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.EnumMaterials;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;

import net.minecraft.world.biome.Biomes;

public class WorldLayerData {
	
	public ArrayList<BiomeLayerData> worldLayerData = new ArrayList<BiomeLayerData>();

	public BiomeLayerData forest = new BiomeLayerData(Biomes.FOREST, 1.5f);
	public BiomeLayerData plains = new BiomeLayerData(Biomes.PLAINS, 1.5f);
	public BiomeLayerData birch_forest = new BiomeLayerData(Biomes.BIRCH_FOREST, 1.5f);
	public BiomeLayerData ocean = new BiomeLayerData(Biomes.OCEAN, 2.5f);
	
	public WorldLayerData() {
		forest.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rock_Andesite.material));
		forest.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rock_Basalt.material));
		forest.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Dolerite.material));
		forest.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Limestone.material));
		
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Limestone.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Dolerite.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Andesite.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Basalt.material));
		
		birch_forest.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Dolerite.material));
		birch_forest.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rock_Basalt.material));
		
		ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Basalt.material));
		ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Dolerite.material));
		
		worldLayerData.add(plains);
		worldLayerData.add(forest);
		worldLayerData.add(birch_forest);
		worldLayerData.add(ocean);
	}
	
}
