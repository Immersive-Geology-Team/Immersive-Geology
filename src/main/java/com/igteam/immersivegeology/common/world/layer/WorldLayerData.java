package com.igteam.immersivegeology.common.world.layer;

import java.util.ArrayList;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;
import com.igteam.immersivegeology.common.world.biome.IGBiomes;

import net.minecraft.world.biome.Biomes;

public class WorldLayerData {
	
	public ArrayList<BiomeLayerData> worldLayerData = new ArrayList<BiomeLayerData>();

	public BiomeLayerData mountains = new BiomeLayerData(IGBiomes.MOUNTAINS, 1.5f);
	public BiomeLayerData plains = new BiomeLayerData(IGBiomes.PLAINS, 1.5f);
	public BiomeLayerData ocean = new BiomeLayerData(IGBiomes.OCEAN, 2.5f);
	public BiomeLayerData deep_ocean = new BiomeLayerData(IGBiomes.DEEP_OCEAN, 2.5f);
	
	public WorldLayerData() {
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rhyolite.material));
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Basalt.material));
		
		mountains.settleLayers();
		
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Limestone.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Limestone.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Granite.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rhyolite.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Basalt.material));
		plains.settleLayers();
		
		ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Limestone.material));
		ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Limestone.material));
		ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Limestone.material));
		ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Basalt.material));
		ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Gabbros.material));
		ocean.settleLayers();
		
		deep_ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Limestone.material));
		deep_ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Limestone.material));
		deep_ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Basalt.material));
		deep_ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Basalt.material));
		deep_ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Gabbros.material));
		deep_ocean.settleLayers();
		
		worldLayerData.add(plains);
		worldLayerData.add(mountains);
		worldLayerData.add(ocean);
	}
	
}
