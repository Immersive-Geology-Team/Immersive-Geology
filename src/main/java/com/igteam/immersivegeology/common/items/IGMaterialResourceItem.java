package com.igteam.immersivegeology.common.items;

import java.util.List;

import javax.annotation.Nullable;

import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.client.menu.helper.IGSubGroup;
import com.igteam.immersivegeology.common.util.IGItemGrabber;
import com.igteam.immersivegeology.common.util.JsonGenerator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

/**
 * Created by Pabilo8 on 27-03-2020.
 */
public class IGMaterialResourceItem extends IGMaterialItem implements IGSubGroup
{
	public IGMaterialResourceItem(Material material, MaterialUseType key) {
		super(key, material);
		this.setRegistryName("item_"+ key.getName() + "_" + material.getName());
		this.itemName = "item."+ key.getName() + "." + material.getName()+".name";
		
		//add this item to the item grabber, that way we can refrence this later.
		IGItemGrabber.inputNewItem(key, material, this);
		JsonGenerator.INSTANCE.generateDefaultItem(material, key);
	}
 
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		StringTextComponent text = new StringTextComponent("");
		if (hasShiftDown() || Minecraft.getInstance().gameSettings.advancedItemTooltips)
		{
			getMaterialFromNBT(stack).getElements().forEach(elementProportion -> text
					.appendText("<hexcol="+elementProportion.getElement().getColor()+":"+elementProportion.getElement().getSymbol()+">")
					.appendText(String.valueOf(elementProportion.getQuantity() > 1?elementProportion.getQuantity(): "")));
			tooltip.add(text);
		}
	}

	public static boolean hasShiftDown() {
		return InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 344);
	}
}
