package com.igteam.immersivegeology.common.items;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.client.menu.helper.IIGSubGroupContained;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Pabilo8 on 27-03-2020.
 * Is used by rock chunks (no special use yet)
 */
public class IGMaterialResourceItem extends IGMaterialItem implements IIGSubGroupContained
{
	public IGMaterialResourceItem(MaterialUseType key,Material... materials)
	{
		super(key, materials);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		StringTextComponent text = new StringTextComponent("");
		if(hasShiftDown()||Minecraft.getInstance().gameSettings.advancedItemTooltips)
		{
			int matAmounts = materials.length;
			Material material = materials[(matAmounts > 1) ? 1 : 0 ];
			
			material.getElements().forEach(elementProportion -> text
					.appendText("<hexcol="+elementProportion.getElement().getColor()+":"+elementProportion.getElement().getSymbol()+">")
					.appendText(String.valueOf(elementProportion.getQuantity() > 1?elementProportion.getQuantity(): "")));
			
			tooltip.add(text);
		}
	}
	
	public static boolean hasShiftDown()
	{
		Minecraft mc = Minecraft.getInstance();
		return InputMappings.isKeyDown(mc.mainWindow.getHandle(), 340)||InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 344);
	}
}
