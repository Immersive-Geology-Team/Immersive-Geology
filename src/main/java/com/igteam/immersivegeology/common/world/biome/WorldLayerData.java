package com.igteam.immersivegeology.common.world.biome;

import java.util.HashMap;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.EnumMaterials;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class WorldLayerData {
	
	public static WorldLayerData INSTANCE = new WorldLayerData();
	
	public HashMap<Biome, BiomeLayerData> worldLayerData = new HashMap<Biome, BiomeLayerData>();

	public BiomeLayerData forest = new BiomeLayerData(Biomes.FOREST, 1.5f);
	public BiomeLayerData plains = new BiomeLayerData(Biomes.PLAINS, 1.5f);
	public BiomeLayerData birch_forest = new BiomeLayerData(Biomes.BIRCH_FOREST, 1.5f);
	public BiomeLayerData ocean = new BiomeLayerData(Biomes.OCEAN, 2.5f);
	
	public WorldLayerData() {
		
		forest.addLayer(0, IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rock_Andesite.material));

		plains.addLayer(0, IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Limestone.material));
		plains.addLayer(1, IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Dolerite.material));
		birch_forest.addLayer(0, IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Dolerite.material));

		ocean.addLayer(0, IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Basalt.material));
		ocean.addLayer(1, IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rock_Dolerite.material));
		
		worldLayerData.put(Biomes.PLAINS, plains);
		worldLayerData.put(Biomes.FOREST, forest);
		worldLayerData.put(Biomes.BIRCH_FOREST, birch_forest);
		
		worldLayerData.put(Biomes.OCEAN, ocean);
	}
	
}
