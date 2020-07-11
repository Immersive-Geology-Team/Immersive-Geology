package com.igteam.immersivegeology.common.world.layer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import com.igteam.immersivegeology.common.materials.EnumMaterials;

import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;

public class BiomeLayerData {

	private ArrayList<IGBaseBlock> layerMap = new ArrayList<IGBaseBlock>();
	private ArrayList<LayerOre> layerOreMap = new ArrayList<LayerOre>();

	private Biome lbiome;
	private float baseHardnessMod;

	public BiomeLayerData(Biome biome, float baseHardnessMod) {
		this.lbiome = biome; 
		this.baseHardnessMod = baseHardnessMod;
	}
	
	public ArrayList<IGBaseBlock> getLayers(){
		return layerMap;
	}

	public void addLayer(IGBaseBlock layerBlock) { 
		layerMap.add(layerBlock);
	}

	public void addLayerOre(int rarity, int veinSize, IGBaseBlock ore) {

	}
	
	public void addMachineOre(float coverage, EnumMaterials ore) {
		layerOreMap.add(new LayerOre(coverage, ore));
	}

	public IGBaseBlock getLayerBlock(int layerID) {
		return layerMap.get(layerID - 1); //take one off the input id as we need to start at 0!
	}

	public LayerOre getLayerOre(int layerID) {
		return layerOreMap.get(layerID);
	}

	public Biome getLbiome() {
		return lbiome;
	}

	public float getBaseHardnessMod() {
		return baseHardnessMod;
	}
	
	public void settleLayers() {
		Collections.reverse(layerMap);
	}

	public class LayerOre {

		private float coverage;
		private EnumMaterials ore;

		public LayerOre(float coverage, EnumMaterials ore) {
			this.coverage = coverage;
			this.ore = ore;
		}

		public EnumMaterials getOre() {
			return ore;
		}

		public void setOre(EnumMaterials ore) { 
			this.ore = ore;
		}

		public float getCoverage() {
			return coverage;
		}

		public void setCoverage(float coverage) {
			this.coverage = coverage;
		}
		
		 
	}

	public int getLayerCount() {
		// TODO Auto-generated method stub
		return this.layerMap.size();
	}
}
