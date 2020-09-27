package com.igteam.immersivegeology.common.world.layer;

import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.blocks.IGMaterialBlock;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BiomeLayerData
{

	private ArrayList<IGMaterialBlock> layerMap = new ArrayList<IGMaterialBlock>();
	private HashMap<Integer, ArrayList<LayerOre>> layerOreMap = new HashMap<Integer, ArrayList<LayerOre>>();

	private Biome lbiome;
	private float baseHardnessMod;
	
	public BiomeLayerData(Biome biome, float baseHardnessMod)
	{
		this.lbiome = biome;
		this.baseHardnessMod = baseHardnessMod;
	}

	public ArrayList<IGMaterialBlock> getLayers()
	{
		return layerMap;
	}

	public void addLayer(IGBaseBlock layerBlock)
	{
		if(layerBlock instanceof IGMaterialBlock) {
			layerMap.add((IGMaterialBlock) layerBlock);
		}
	}

	/**
	 * @param layerID  - Layer the ore is to appear in
	 * @param coverage - How common is the ore; min is 0.2f max is 0.65f, higher or lower will be ignored and set to the closest acceptable value. (this is to allow ores to spawn in a controlled amount) if it was 1 every rock would be that ore, anything lower than a 0.2 would almost never show up
	 * @param ore      - the ore to spawn - choose from the EnumMaterials
	 */
	public void addMachineOre(int layerID, float coverage, EnumMaterials ore)
	{
		if(layerOreMap.containsKey(layerID))
		{
			ArrayList<LayerOre> oreList = layerOreMap.get(layerID);
			oreList.add(new LayerOre(coverage, ore, layerID));
			layerOreMap.put(layerID, oreList);
		}
		else
		{
			ArrayList<LayerOre> firstOre = new ArrayList<LayerOre>();
			firstOre.add(new LayerOre(coverage, ore, layerID));
			layerOreMap.put(layerID, firstOre);
		}
	}

	public IGMaterialBlock getLayerBlock(int layerID)
	{
		return layerMap.get(layerID-1); //take one off the input id as we need to start at 0!
	}

	public int layerOreSize(int layerID)
	{
		return layerOreMap.get(layerID).size();
	}

	public ArrayList<LayerOre> getLayerOre(int layerID)
	{
		if(layerOreMap.containsKey(layerID))
		{
			return layerOreMap.get(layerID);
		}
		else
		{
			return null;
		}
	}


	public Biome getLbiome()
	{
		return lbiome;
	}

	public float getBaseHardnessMod()
	{
		return baseHardnessMod;
	}

	public void settleLayers()
	{
		Collections.reverse(layerMap);
	}

	public class LayerOre
	{

		private float coverage;
		private EnumMaterials ore;
		private int setLayer;


		public LayerOre(float coverage, EnumMaterials ore, int layer)
		{
			this.coverage = coverage;
			this.ore = ore;
			this.setSetLayer(layer);
		}

		public EnumMaterials getOre()
		{
			return ore;
		}

		public void setOre(EnumMaterials ore)
		{
			this.ore = ore;
		}

		public float getCoverage()
		{
			return coverage;
		}

		public void setCoverage(float coverage)
		{
			this.coverage = coverage;
		}

		public int getSetLayer()
		{
			return setLayer;
		}

		public void setSetLayer(int setLayer)
		{
			this.setLayer = setLayer;
		}


	}

	public int getLayerCount()
	{
		return this.layerMap.size();
	}
	
	public BiomeLayerData addLayerData(IGBaseBlock...layerBlocks) {
		for(IGBaseBlock layerBlock : layerBlocks) {
			addLayer(layerBlock);
		}
		return this;
	}
	
	/**
	 * @apiNote Only use after layer has been added!
	 * @param layerID The lowest ID is 1, it relates to the lowest layer in the world
	 * @param objects - input the EnumMaterial type before you input the float rarity
	 */
	public BiomeLayerData addLayerOreData(int layerID, Object...objects) {
		for(int index = 0; index < (objects.length - 1); index+=2) {
			addMachineOre(layerID, (float)objects[index+1], (EnumMaterials) objects[index]);
		} 
		
		return this;
	}
	
	/**
	 * @apiNote Only use after layer has been added!
	 * @param layerID The lowest ID is 1, it relates to the lowest layer in the world
	 * @param objects - input the EnumMaterial type before you input the float rarity
	 */
	public BiomeLayerData addLayerOreData(int[] layerIDs, Object...objects) {
		
		for(int layerID : layerIDs) {
			for(int index = 0; index < (objects.length - 1); index+=2) {
				addMachineOre(layerID, (float)objects[index+1], (EnumMaterials) objects[index]);
			} 
		}
		return this;
	}

	public BiomeLayerData build() {
		settleLayers();
		return this;
	}
}
