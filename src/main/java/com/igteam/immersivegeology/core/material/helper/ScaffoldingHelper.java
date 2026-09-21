package com.igteam.immersivegeology.core.material.helper;

import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.minecraft.block.Block;

import java.util.Locale;

public class ScaffoldingHelper
{
	public enum MetalScaffoldingType
	{
		STANDARD,
		GRATE_TOP,
		WOODEN_TOP
	}

	private final MaterialHelper scaffoldingMaterial;

	public ScaffoldingHelper(MaterialHelper materialHelper)
	{
		this.scaffoldingMaterial = materialHelper;
	}

	public ScaffoldingHelper(MaterialInterface<?> material)
	{
		this(material.instance());
	}

	private Block get(MetalScaffoldingType type)
	{
		String key = BlockCategoryFlags.SCAFFOLDING.getRegistryKey(scaffoldingMaterial)
				+"_"+type.name().toLowerCase(Locale.ROOT);
		Block block = IGContent.getBlock(key);
		if(block==null) IGLib.IG_LOGGER.error("Attempting to get a missing block? {}", key);
		return block;
	}

	public Block getDefault()
	{
		return get(MetalScaffoldingType.STANDARD);
	}

	public Block getGrate()
	{
		return get(MetalScaffoldingType.GRATE_TOP);
	}

	public Block getWoodenTop()
	{
		return get(MetalScaffoldingType.WOODEN_TOP);
	}
}
