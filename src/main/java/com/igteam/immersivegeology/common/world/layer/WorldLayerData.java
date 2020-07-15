package com.igteam.immersivegeology.common.world.layer;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.materials.EnumOreBearingMaterials;
import com.igteam.immersivegeology.common.util.IGBlockGrabber;
import com.igteam.immersivegeology.common.world.biome.IGBiomes;

import java.util.ArrayList;

public class WorldLayerData
{

	public ArrayList<BiomeLayerData> worldLayerData = new ArrayList<BiomeLayerData>();

	public BiomeLayerData mountains = new BiomeLayerData(IGBiomes.MOUNTAINS, 1f);
	public BiomeLayerData badlands = new BiomeLayerData(IGBiomes.BADLANDS, 1f);
	public BiomeLayerData lush_mountains = new BiomeLayerData(IGBiomes.LUSH_MOUNTAINS, 1f);
	public BiomeLayerData flooded_mountains = new BiomeLayerData(IGBiomes.FLOODED_MOUNTAINS, 1f);
	public BiomeLayerData plains = new BiomeLayerData(IGBiomes.PLAINS, 1.5f);
	public BiomeLayerData ocean = new BiomeLayerData(IGBiomes.OCEAN, 2.5f);
	public BiomeLayerData deep_ocean = new BiomeLayerData(IGBiomes.DEEP_OCEAN, 2.5f);
	public BiomeLayerData desert = new BiomeLayerData(IGBiomes.DESERT, 1f);

	public WorldLayerData()
	{
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));

		for(int id = 0; id < mountains.getLayers().size(); id++)
		{
			mountains.addMachineOre(id, 0.52f, EnumOreBearingMaterials.Gold);
			mountains.addMachineOre(id, 0.38f, EnumOreBearingMaterials.Hematite);
			mountains.addMachineOre(id, 0.35f, EnumOreBearingMaterials.Cuprite);
			mountains.addMachineOre(id, 0.15f, EnumOreBearingMaterials.Uraninite);
		}
		
		mountains.settleLayers();

		lush_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		lush_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		lush_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		lush_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		lush_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));

		lush_mountains.settleLayers();

		flooded_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		flooded_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		flooded_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));
		flooded_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		flooded_mountains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		
		for(int id = 0; id < mountains.getLayers().size(); id++)
		{
			flooded_mountains.addMachineOre(id, 0.82f, EnumOreBearingMaterials.Gold);
			flooded_mountains.addMachineOre(id, 0.38f, EnumOreBearingMaterials.Hematite);
			flooded_mountains.addMachineOre(id, 0.35f, EnumOreBearingMaterials.Cuprite);
			flooded_mountains.addMachineOre(id, 0.15f, EnumOreBearingMaterials.Uraninite);
		}
		
		flooded_mountains.settleLayers();

		desert.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Pegamite.material));
		desert.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Pegamite.material));
		desert.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));
		desert.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));
		desert.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		desert.settleLayers();

		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));
		plains.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		plains.settleLayers();

		ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Gabbros.material));
		ocean.settleLayers();

		deep_ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		deep_ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		deep_ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		deep_ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		deep_ocean.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Gabbros.material));
		deep_ocean.settleLayers();


		badlands.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));

		badlands.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Gabbros.material));

		badlands.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Gabbros.material));

		badlands.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		badlands.addMachineOre(2, 0.25f, EnumOreBearingMaterials.Uraninite);
		badlands.addMachineOre(2, 0.50f, EnumOreBearingMaterials.Ilmenite);
		badlands.addMachineOre(2, 0.60f, EnumOreBearingMaterials.Hubnerite);

		badlands.addLayer(IGBlockGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		//These ores Spawn in badlands bioems, copper is fairly rare here, but does occur (eg cuprite with cover value of .3f
		badlands.addMachineOre(1, 0.30f, EnumOreBearingMaterials.Cuprite);//the first ore will have the most chance of spawning as it spawns FIRST
		badlands.addMachineOre(1, 0.50f, EnumOreBearingMaterials.Magnetite);
		badlands.addMachineOre(1, 0.65f, EnumOreBearingMaterials.Gold);

		badlands.settleLayers();

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
