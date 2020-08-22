package com.igteam.immersivegeology.common.items;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class IGMaterialYieldItem extends IGMaterialResourceItem
{

	public int stoneYield;
	public int oreYield;

	public IGMaterialYieldItem(MaterialUseType key, int maxStoneYeild, int maxOreYeild, Material... materials)
	{
		super(key, materials);
		stoneYield = maxStoneYeild;
		oreYield = maxOreYeild;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if(hasShiftDown()) {
			tooltip.add(new StringTextComponent(""));
			StringTextComponent text = new StringTextComponent("Stone Yield: "+String.valueOf(getMaxStoneYield())+"mb");
			StringTextComponent text2 = new StringTextComponent("Ore Yield: "+String.valueOf(getMaxOreYield())+"mb");
			tooltip.add(text);
			if(getMaxOreYield() > 0)
			{
				tooltip.add(text2);
			}
		}
	}

	public int getMaxStoneYield()
	{
		return stoneYield;
	}

	public int getMaxOreYield()
	{
		return oreYield;
	}
	
}
