package com.igteam.immersivegeology.common;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.common.blocks.IGBaseBlock;
import net.minecraft.item.Item;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * concept, design, basically everything by
 * @author Muddykat
 * merging classes, registry integration, lambdas and thingyfiers by
 * @author Pabilo8
 *
 * Here will be all the registry grab methods, currently Blocks and Items, in future TileEntities, Fluids
 */
public class IGRegistryGrabber
{
	/**
	 *
	 * @param type of the item
	 * @param materials the item is made of
	 * @return item that matches given type and materials
	 */
	@Nonnull
	public static Item getIGItem(@Nonnull MaterialUseType type, @Nonnull Material... materials)
	{
		StringBuilder builder = new StringBuilder(type.getName());
		Arrays.stream(materials).forEach(material -> builder.append("_").append(material.getName()));
		return IGContent.registeredIGItems.getOrDefault(builder.toString(), IGContent.registeredIGItems.values().stream().findAny().get());
	}

	/**
	 *
	 * @param type of the block
	 * @param materials the block is made of
	 * @return item that matches given type and materials
	 */
	@Nonnull
	public static IGBaseBlock grabBlock(@Nonnull MaterialUseType type, @Nonnull Material... materials)
	{
		StringBuilder builder = new StringBuilder(type.getName());
		Arrays.stream(materials).forEach(material -> builder.append("_").append(material.getName()));

		//Yes, I know it will throw an error, but only if you use it wrong ^^
		return IGContent.registeredIGBlocks.getOrDefault(builder.toString(), IGContent.registeredIGBlocks.values().stream().findAny().get());
	}

}
