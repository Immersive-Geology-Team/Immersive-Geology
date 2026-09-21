package com.igteam.immersivegeology.core.material.data.stone.vanilla;

import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class MaterialMCStone extends MaterialStone
{
	public MaterialMCStone()
	{
		super();
		this.name = "stone";
		this.stoneFormation = StoneFormation.MINECRAFT_STONE;
		this.textureName = "stone";
		addFlags(MaterialFlags.EXISTING_IMPLEMENTATION, ModFlags.MINECRAFT);
	}

	@Override
	public int getColor(IFlagType<?> flag, int secondaryColors)
	{
		return flag==BlockCategoryFlags.ORE_BLOCK?0xffffff: 0x7e7e7e;
	}

	@Override
	public IBlockState getHostState()
	{
		return Blocks.STONE.getDefaultState();
	}
}
