package com.igteam.immersivegeology.common.block.helper;

import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Collection;

public interface IOreBlock
{
	IBlockState getIGDefaultBlockState();

	Collection<MaterialInterface<?>> getMaterials();

	String getIGDescriptionId();

	Item asIGItem();

	Block asIGBlock();

	MaterialInterface<?> getMaterial(MaterialTexture texture);

	default MaterialInterface<?> getOreMaterial()
	{
		return getMaterial(MaterialTexture.overlay);
	}

	default MaterialInterface<?> getStoneMaterial()
	{
		return getMaterial(MaterialTexture.base);
	}

	OreRichness getOreRichness();

	StoneFormation getStoneFormation();
}
