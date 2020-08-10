package com.igteam.immersivegeology.common.items;

import java.util.List;

import javax.annotation.Nullable;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class IGMaterialYeildItem extends IGMaterialResourceItem {

	public int stoneYeild;
	public int oreYeild;
	public IGMaterialYeildItem(MaterialUseType key,int maxStoneYeild, int maxOreYeild, Material... materials) {
		super(key, materials);
		stoneYeild = maxStoneYeild;
		oreYeild = maxOreYeild;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if(hasShiftDown()) {
			tooltip.add(new StringTextComponent(""));
			StringTextComponent text = new StringTextComponent("Stone Yeild: " + String.valueOf(getMaxStoneYeild()) + "mb");
			StringTextComponent text2 = new StringTextComponent("Ore Yeild: " + String.valueOf(getMaxOreYeild()) + "mb");
			tooltip.add(text);
			if(getMaxOreYeild() > 0) {
				tooltip.add(text2);
			}
		}
	}
	
	public int getMaxStoneYeild() {
		return stoneYeild;
	}
	
	public int getMaxOreYeild() {
		return oreYeild;
	}
	
}
