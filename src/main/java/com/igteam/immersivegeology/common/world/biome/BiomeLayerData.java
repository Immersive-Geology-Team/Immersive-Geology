package com.igteam.immersivegeology.common.world.biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;

import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;

public class BiomeLayerData {

	private HashMap<Integer, IGBaseBlock> layerMap = new HashMap<Integer, IGBaseBlock>();
	private HashMap<Integer, LayerOre> layerOreMap = new HashMap<Integer, LayerOre>();

	private Biome lbiome;
	private float baseHardnessMod;

	public BiomeLayerData(Biome biome, float baseHardnessMod) {
		this.lbiome = biome;
		this.baseHardnessMod = baseHardnessMod;
	}
	
	public List<IGBaseBlock> getLayers(){
		return layerMap.values().stream().collect(Collectors.toList());
	}

	public void addLayer(int layerID, IGBaseBlock layerBlock) {
		layerMap.put(layerID, layerBlock);
	}

	public void addLayerOre(int layerID, int rarity, int veinSize, IGBaseBlock ore) {
		layerOreMap.put(layerID, new LayerOre(rarity, veinSize, ore));
	}

	public IGBaseBlock getLayerBlock(int layerID) {
		return layerMap.get(layerID);
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

	public class LayerOre {

		private int rarity, veinSize;
		private Block ore;

		public LayerOre(int rarity, int veinSize, Block ore) {
			this.rarity = rarity;
			this.veinSize = veinSize;
			this.ore = ore;
		}

		public int getRarity() {
			return rarity;
		}

		public void setRarity(int rarity) {
			this.rarity = rarity;
		}

		public int getVeinSize() {
			return veinSize;
		}

		public void setVeinSize(int veinSize) {
			this.veinSize = veinSize;
		}

		public Block getOre() {
			return ore;
		}

		public void setOre(Block ore) {
			this.ore = ore;
		}
	}

	public int getLayerCount() {
		// TODO Auto-generated method stub
		return this.layerMap.size();
	}
}
