package com.igteam.immersivegeology.common.items;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.sun.jna.platform.KeyboardUtils;
import net.minecraft.client.Minecraft;
import com.igteam.immersivegeology.client.menu.helper.IGSubGroup;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.util.IGItemGrabber;
import com.igteam.immersivegeology.common.util.ItemJsonGenerator;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;

import java.io.IOException;
import java.util.List;

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
		
		ItemJsonGenerator generate = new ItemJsonGenerator();
		try {
			generate.generateDefaultItem("item_"+ key.getName() + "_" + material.getName(), key.getName(), material.getMaterialType().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
