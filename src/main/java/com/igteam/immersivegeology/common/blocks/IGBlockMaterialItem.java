package com.igteam.immersivegeology.common.blocks;

import blusunrize.immersiveengineering.common.items.IEItemInterfaces.IColouredItem;
import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.util.IGMathHelper;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.igteam.immersivegeology.common.items.IGMaterialResourceItem.hasShiftDown;

public class IGBlockMaterialItem extends IGBlockItem implements IColouredItem
{

	public MaterialUseType subtype = MaterialUseType.STORAGE_BLOCK;
	public Material[] materials;
	public boolean isSlab = false;

	public IGBlockMaterialItem(Block b, Properties props, ItemSubGroup sub)
	{
		super(b, props.group(ImmersiveGeology.IG_ITEM_GROUP), sub);
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack)
	{
		ArrayList<String> localizedNames = new ArrayList<>();
		for(Material m : materials)
			localizedNames.add(I18n.format("material."+m.getModID()+"."+m.getName()+".name"));
		//If slab, pass the block name as argument to slab translation, else return it
		if(isSlab)
			return new TranslationTextComponent("block."+ImmersiveGeology.MODID+".slab.name",
					new TranslationTextComponent("block."+ImmersiveGeology.MODID+"."+subtype.getName().toLowerCase(Locale.ENGLISH)+".name", localizedNames.toArray(new String[localizedNames.size()]))
			);
		else
			return new TranslationTextComponent("block."+ImmersiveGeology.MODID+"."+subtype.getName().toLowerCase(Locale.ENGLISH)+".name", localizedNames.toArray(new String[localizedNames.size()]));

	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		StringTextComponent text = new StringTextComponent("");
		if(hasShiftDown()||Minecraft.getInstance().gameSettings.advancedItemTooltips)
		{
			for(Material material : materials)
				material.getElements().forEach(elementProportion -> text
						.appendText("<hexcol="+elementProportion.getElement().getColor()+":"+elementProportion.getElement().getSymbol()+">")
						.appendText(String.valueOf(elementProportion.getQuantity() > 1?elementProportion.getQuantity(): ""))
				);
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
		return materials[IGMathHelper.clamp(pass, 0, materials.length-1)].getColor(0);
	}
}
