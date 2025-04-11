package com.igteam.immersivegeology.core.material.data.mineral;


import com.igteam.immersivegeology.client.helper.IGVeinTextureType;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.tags.BiomeTags;

import java.util.Optional;

public class MaterialBituminous extends MaterialMineral
{

	public MaterialBituminous()
	{
		super();
		this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
		this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
		removeMaterialFlags(ItemCategoryFlags.values());
		removeMaterialFlags(BlockCategoryFlags.values());
		addFlags(ItemCategoryFlags.NORMAL_ORE);
		addFlags(BlockCategoryFlags.ORE_BLOCK, BlockCategoryFlags.STORAGE_BLOCK);

		addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
		setBurntime(800);
		CONFIG = new MineralConfig(30, 50, 1, -64, 320, 1000, 0.75,false, Optional.of(BiomeTags.IS_OVERWORLD), IGGenerationType.BANDED);
		this.colorFunction = (flag,v) -> 0xff242227;
		addGenerationFriend(MaterialBituminous::getFriends, 300);
	}

	private static MaterialHelper getFriends(int height){
		MaterialHelper selected = null;
		if(height > 55) selected = MineralEnum.Lignite.instance();
		if(height < 0) selected = MineralEnum.Anthracite.instance();

		return selected;
	}


	@Override
	public IGVeinTextureType getVeinTextureType()
	{
		return IGVeinTextureType.LAYERED;
	}

	@Override
	public float getNoiseProbability()
	{
		return 0.17510986f;
	}
}
