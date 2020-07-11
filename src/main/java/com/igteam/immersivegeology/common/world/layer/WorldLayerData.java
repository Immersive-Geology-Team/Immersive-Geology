package com.igteam.immersivegeology.common.world.layer;

import java.util.ArrayList;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.materials.EnumOreBearingMaterials;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;
import com.igteam.immersivegeology.common.world.biome.IGBiomes;

import net.minecraft.world.biome.Biomes;

public class WorldLayerData {
	
	public ArrayList<BiomeLayerData> worldLayerData = new ArrayList<BiomeLayerData>();

	public BiomeLayerData mountains = new BiomeLayerData(IGBiomes.MOUNTAINS, 1f);
	public BiomeLayerData badlands = new BiomeLayerData(IGBiomes.BADLANDS, 1f);
	public BiomeLayerData lush_mountains = new BiomeLayerData(IGBiomes.LUSH_MOUNTAINS, 1f);
	public BiomeLayerData flooded_mountains = new BiomeLayerData(IGBiomes.FLOODED_MOUNTAINS, 1f);
	public BiomeLayerData plains = new BiomeLayerData(IGBiomes.PLAINS, 1.5f);
	public BiomeLayerData ocean = new BiomeLayerData(IGBiomes.OCEAN, 2.5f);
	public BiomeLayerData deep_ocean = new BiomeLayerData(IGBiomes.DEEP_OCEAN, 2.5f);
	public BiomeLayerData desert = new BiomeLayerData(IGBiomes.DESERT, 1f);
	
	public WorldLayerData() {
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rhyolite.material));
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Basalt.material));
		//mountains.addLayerOre(rarity, veinSize, ore); 
		mountains.addMachineOre(0.12f, EnumOreBearingMaterials.Gold);
		mountains.addMachineOre(0.38f, EnumOreBearingMaterials.Hematite); 
		mountains.addMachineOre(0.35f, EnumOreBearingMaterials.Cuprite);
		mountains.addMachineOre(0.15f, EnumOreBearingMaterials.Uraninite);  
		 
		mountains.settleLayers();
		
		lush_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		lush_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		lush_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		lush_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rhyolite.material));
		lush_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Basalt.material));
		lush_mountains.settleLayers();
		
		flooded_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		flooded_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		flooded_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));
		flooded_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		flooded_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		flooded_mountains.settleLayers();
		
		desert.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Pegamite.material));
		desert.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Pegamite.material));
		desert.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));
		desert.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));
		desert.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		desert.settleLayers();
		
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Limestone.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Limestone.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Rhyolite.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK,  EnumMaterials.Granite.material));
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
		
		badlands.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));
		badlands.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Gabbros.material));
		badlands.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Gabbros.material));
		badlands.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		badlands.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		badlands.settleLayers();
		badlands.addMachineOre(0.62f, EnumOreBearingMaterials.Gold);
		badlands.addMachineOre(0.08f, EnumOreBearingMaterials.Cuprite);
		badlands.addMachineOre(0.30f, EnumOreBearingMaterials.Magnetite);
		
		 
		worldLayerData.add(badlands);
		worldLayerData.add(desert);
		worldLayerData.add(lush_mountains);
		worldLayerData.add(flooded_mountains);
		worldLayerData.add(plains); 
		worldLayerData.add(mountains);
		worldLayerData.add(ocean);
		worldLayerData.add(deep_ocean);
	}
	
}
