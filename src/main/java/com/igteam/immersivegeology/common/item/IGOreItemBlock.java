package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.helper.OreBlockMeta;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.block.ore.IGOreBlock;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class IGOreItemBlock extends ItemBlock
{
	public IGOreItemBlock(IGOreBlock block)
	{
		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getTranslationKey(ItemStack stack)
	{
		int meta = stack.getMetadata();
		OreRichness richness = OreBlockMeta.richness(meta);
		MineralWeathering weathering = OreBlockMeta.weathering(meta);
		String base = super.getTranslationKey(stack);
		if(weathering==MineralWeathering.PRISTINE) return base+"."+richness.getSanitizedName();
		return base+"."+richness.getSanitizedName()+"."+weathering.getSanitizedName();
	}
}
