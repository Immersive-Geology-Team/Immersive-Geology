package com.igteam.immersivegeology.common.items;

import com.igteam.immersivegeology.api.materials.MaterialUseType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Pabilo8 on 27-03-2020.
 */
public class IGMaterialResourceItem extends IGMaterialItem
{
	public IGMaterialResourceItem(MaterialUseType type)
	{
		super(type);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		StringBuilder builder = new StringBuilder();
		getMaterialFromNBT(stack).getElements().forEach(elementProportion -> builder.append(elementProportion.getElement().getName()).append(elementProportion.getQuantity()));
		tooltip.add(new StringTextComponent(builder.toString()));
	}


}
