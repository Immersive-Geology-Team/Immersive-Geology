package com.igteam.immersivegeology.common.blocks;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces.IColouredItem;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.materials.EnumMaterials;
import com.igteam.immersivegeology.common.materials.EnumOreBearingMaterials;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

import static com.igteam.immersivegeology.common.items.IGMaterialResourceItem.hasShiftDown;

public class IGBlockMaterialItem extends IGBlockItem implements IColouredItem
{

	public MaterialUseType subtype = MaterialUseType.STORAGE;
	public Material material = EnumMaterials.Empty.material;
	public boolean isSlab = false;

	public EnumOreBearingMaterials overlay = null;

	public IGBlockMaterialItem(Block b, Properties props, ItemSubGroup sub)
	{
		super(b, props.group(ImmersiveGeology.IG_ITEM_GROUP), sub);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack)
	{
		TranslationTextComponent matName = new TranslationTextComponent("material."+material.getModID()+"."+material.getName()+".name");
		TranslationTextComponent typeName = new TranslationTextComponent("block."+ImmersiveGeology.MODID+"."+subtype.getName().toLowerCase(Locale.ENGLISH)+".name", matName);
		return isSlab?new TranslationTextComponent("block."+ImmersiveGeology.MODID+".slab.name", typeName): typeName;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		StringTextComponent text = new StringTextComponent("");
		if(hasShiftDown()||Minecraft.getInstance().gameSettings.advancedItemTooltips)
		{
			material.getElements().forEach(elementProportion -> text
					.appendText("<hexcol="+elementProportion.getElement().getColor()+":"+elementProportion.getElement().getSymbol()+">")
					.appendText(String.valueOf(elementProportion.getQuantity() > 1?elementProportion.getQuantity(): "")));
			tooltip.add(text);
		}
	}

	@Override
	public boolean hasCustomItemColours()
	{
		return true;
	}

	@Override
	public int getColourForIEItem(ItemStack stack, int pass)
	{
		if(pass==0)
		{
			return material.getColor(0);
		}
		else
		{
			if(overlay!=null)
			{
				return overlay.getColor();
			}
			else
			{
				return material.getColor(0);
			}
		}
	}
}
