package com.igteam.immersivegeology.common.block;

import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;

public class IGGenericBlock extends Block
{
	protected final BlockCategoryFlags category;
	protected final MaterialInterface<?> material;

	public IGGenericBlock(BlockCategoryFlags category, MaterialInterface<?> material)
	{
		super(materialFor(category));
		this.category = category;
		this.material = material;

		setHardness(hardnessFor(category));
		setResistance(resistanceFor(category));
	}

	private static Material materialFor(BlockCategoryFlags category)
	{
		return switch(category)
		{
			case STORAGE_BLOCK, SHEETMETAL_BLOCK, SHEETMETAL_SLAB, SHEETMETAL_STAIRS,
				 ENGINEERING_BLOCK, ADVANCED_ENGINEERING_BLOCK, SCAFFOLDING -> Material.IRON;
			case DUST_BLOCK -> Material.SAND;
			default -> Material.ROCK;
		};
	}

	private static float hardnessFor(BlockCategoryFlags category)
	{
		return switch(category)
		{
			case DUST_BLOCK -> 0.5F;
			case SCAFFOLDING -> 2.0F;
			case STORAGE_BLOCK, ENGINEERING_BLOCK, ADVANCED_ENGINEERING_BLOCK -> 5.0F;
			default -> 3.0F;
		};
	}

	private static float resistanceFor(BlockCategoryFlags category)
	{
		return switch(category)
		{
			case DUST_BLOCK -> 2.5F;
			case STORAGE_BLOCK, ENGINEERING_BLOCK, ADVANCED_ENGINEERING_BLOCK -> 10.0F;
			default -> 5.0F;
		};
	}

	public BlockCategoryFlags getCategory()
	{
		return category;
	}

	public MaterialInterface<?> getMaterial()
	{
		return material;
	}

	public int getColor(int tintIndex, IBlockState state)
	{
		return material.getColor(category, tintIndex);
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return category.getRenderLayer();
	}
}
