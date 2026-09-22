package com.igteam.immersivegeology.core.lib;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.igteam.immersivegeology.core.material.helper.material.IGBlockProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IGLib
{
	public static final String MODID = "immersivegeology";
	public static final String NAME = "Immersive Geology";
	public static final String VERSION = "@VERSION@";

	public static final String PROXY_COMMON = "com.igteam.immersivegeology.core.CommonProxy";
	public static final String PROXY_CLIENT = "com.igteam.immersivegeology.core.ClientProxy";

	public static final String DEPENDENCIES = "required-after:immersiveengineering";

	public static final Logger IG_LOGGER = LogManager.getLogger(NAME);

	public static final IGBlockProperties STONE_DECO_PROPS =
			IGBlockProperties.of(net.minecraft.block.material.Material.ROCK);
	public static final IGBlockProperties CRYSTAL_DECO_PROPS =
			IGBlockProperties.of(net.minecraft.block.material.Material.GLASS);
	public static final IGBlockProperties DEFAULT_METAL_PROPERTIES =
			IGBlockProperties.of(net.minecraft.block.material.Material.IRON);
	public static final IGBlockProperties SHEETMETAL_PROPERTIES =
			IGBlockProperties.of(net.minecraft.block.material.Material.IRON);
	public static final IGBlockProperties METAL_PROPERTIES_NO_OCCLUSION =
			IGBlockProperties.of(net.minecraft.block.material.Material.IRON);

	public static final int SLURRY_TO_CRYSTAL_MB = 144;
	public static final int SULFUR_OUTGAS = 25;
	public static final float TWO_ACID_USED_MULTIPLIER = 0.5f;
	public static final float THREE_ACID_USED_MULTIPLIER = 0.5f;
	public static final int ACID_RECOVERED_FROM_SLURRY = 120;
	public static final int ACID_TO_SLURRY_AMOUNT = 250;
	public static final int SLURRY_FROM_ACID_AMOUNT = 216;
	public static final int DUST_TO_SLURRY_AMOUNT = 1;
	public static final int COMPOUND_FROM_ACID_AMOUNT = 1;
	public static final int ACID_TO_COMPOUND_AMOUNT = 125;
	public static final int COMPOUND_ACID_TO_DUST_AMOUNT = 1;
	public static final int ACID_TO_DUST_AMOUNT = 125;
	public static final int DUST_FROM_COMPOUND_ACID_AMOUNT = 1;
	public static final int PELLETIZER_DEFAULT_TIME = 600;

	private static final List<MaterialInterface<?>> GEOLOGY_MATERIALS = buildMaterialList();

	private static List<MaterialInterface<?>> buildMaterialList()
	{
		List<MaterialInterface<?>> list = new ArrayList<>();
		Collections.addAll(list, MetalEnum.values());
		Collections.addAll(list, MineralEnum.values());
		Collections.addAll(list, StoneEnum.values());
		Collections.addAll(list, MiscEnum.values());
		return Collections.unmodifiableList(list);
	}

	public static List<MaterialInterface<?>> getGeologyMaterials()
	{
		return GEOLOGY_MATERIALS;
	}
}
