package com.igteam.immersivegeology.common.world.layer;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGRegistryGrabber;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
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
		mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));

		for(int id = 0; id < mountains.getLayers().size(); id++)
		{
			mountains.addMachineOre(id, 0.32f, EnumMaterials.Gold);
			mountains.addMachineOre(id, 0.38f, EnumMaterials.Hematite);
			mountains.addMachineOre(id, 0.35f, EnumMaterials.Cuprite);
			mountains.addMachineOre(id, 0.15f, EnumMaterials.Uraninite);
		}
		mountains.settleLayers();

		lush_mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		lush_mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		lush_mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		lush_mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		lush_mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		for(int id = 0; id < lush_mountains.getLayers().size(); id++)
		{
			lush_mountains.addMachineOre(id, 0.32f, EnumMaterials.Gold);
			lush_mountains.addMachineOre(id, 0.38f, EnumMaterials.Hematite);
			lush_mountains.addMachineOre(id, 0.35f, EnumMaterials.Cuprite);
			lush_mountains.addMachineOre(id, 0.15f, EnumMaterials.Uraninite);
		}
		lush_mountains.settleLayers();

		flooded_mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		flooded_mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		flooded_mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));
		flooded_mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Marble.material));
		flooded_mountains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		for(int id = 0; id < lush_mountains.getLayers().size(); id++)
		{
			flooded_mountains.addMachineOre(id, 0.72f, EnumMaterials.Gold);
			flooded_mountains.addMachineOre(id, 0.38f, EnumMaterials.Hematite);
			flooded_mountains.addMachineOre(id, 0.35f, EnumMaterials.Cuprite);
			flooded_mountains.addMachineOre(id, 0.15f, EnumMaterials.Uraninite);
		}
		flooded_mountains.settleLayers();

		desert.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Pegamite.material));
		desert.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Pegamite.material));
		desert.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));
		desert.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));
		desert.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		desert.settleLayers();

		plains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		plains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		plains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Rhyolite.material));
		plains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));
		plains.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		plains.settleLayers();

		ocean.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		ocean.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		ocean.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		ocean.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		ocean.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Gabbros.material));
		ocean.settleLayers();

		deep_ocean.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		deep_ocean.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Limestone.material));
		deep_ocean.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		deep_ocean.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		deep_ocean.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Gabbros.material));
		deep_ocean.settleLayers();


		badlands.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Granite.material));

		badlands.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Gabbros.material));

		badlands.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Gabbros.material));

		badlands.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		badlands.addMachineOre(2, 0.25f, EnumMaterials.Uraninite);
		badlands.addMachineOre(2, 0.30f, EnumMaterials.Ilmenite);
		badlands.addMachineOre(2, 0.30f, EnumMaterials.Hubnerite);

		badlands.addLayer(IGRegistryGrabber.grabBlock(MaterialUseType.ROCK, EnumMaterials.Basalt.material));
		badlands.addMachineOre(1, 0.30f, EnumMaterials.Cuprite);//the first ore will have the most chance of spawning as it spawns FIRST
		badlands.addMachineOre(1, 0.50f, EnumMaterials.Magnetite);
		badlands.addMachineOre(1, 0.65f, EnumMaterials.Gold);

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
