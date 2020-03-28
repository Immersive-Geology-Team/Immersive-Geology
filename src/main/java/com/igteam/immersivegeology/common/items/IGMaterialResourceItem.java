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
		StringTextComponent text = new StringTextComponent("");
		getMaterialFromNBT(stack).getElements().forEach(elementProportion -> text
				.appendText("<hexcol="+elementProportion.getElement().getColor()+":"+elementProportion.getElement().getSymbol()+">")
				.appendText(String.valueOf(elementProportion.getQuantity() > 1?elementProportion.getQuantity(): "")));
		tooltip.add(text);
	}


}
