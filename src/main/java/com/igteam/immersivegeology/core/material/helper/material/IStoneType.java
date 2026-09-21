package com.igteam.immersivegeology.core.material.helper.material;

import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import net.minecraft.block.state.IBlockState;

import java.util.Locale;
import java.util.Set;

public interface IStoneType extends MaterialInterface<MaterialStone>
{
	int index();

	boolean isStoneTypeValid();

	boolean isVanilla();

	String getRegistryPrefix();

	Set<String> getDimensions();

	boolean declaresPresence();

	IBlockState getHostState();

	static String backdropPaletteKey(MaterialInterface<?> stone)
	{
		if(stone instanceof IStoneType)
			return ((IStoneType)stone).getRegistryPrefix()+stone.getName().toLowerCase(Locale.ROOT);
		return stone.getName().toLowerCase(Locale.ROOT);
	}

	default boolean excludesOre(MaterialHelper ore)
	{
		return false;
	}
}
