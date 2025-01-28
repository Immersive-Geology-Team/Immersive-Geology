package com.igteam.immersivegeology.common.menu;

import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import com.igteam.immersivegeology.common.item.blueprint.IGBlueprintSettings;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SchematicInventory extends SimpleContainer
{
	private final List<IMultiblock> schematics;
	private final SchematicsContainerMenu menu;
	private static ArrayList<IMultiblock> exceptions = new ArrayList<>();
	public SchematicInventory(SchematicsContainerMenu container, List<IMultiblock> schematics)
	{
		super(schematics.size());
		this.schematics = schematics;
		this.menu = container;
	}

	public void updateOutputs(Container inputInventory)
	{
		//Get input items
		NonNullList<ItemStack> inputs = NonNullList.withSize(inputInventory.getContainerSize()-1, ItemStack.EMPTY);
		for(int i = 0; i < inputs.size(); i++)
			inputs.set(i, inputInventory.getItem(i));
		//Iterate Recipes and set output slots
		if(inputs.get(0).getCount() > 0)
		{
			for(int i = 0; i < this.schematics.size(); i++)
			{
				IMultiblock mb = schematics.get(i);
				if(mb instanceof TemplateMultiblock template)
				{
					ItemStack blueprint = new ItemStack(MiscEnum.Blueprint.getItem(ItemCategoryFlags.BLUEPRINT));
					IGBlueprintSettings settings = new IGBlueprintSettings(blueprint);
					settings.setMultiblock(template);
					settings.setMirror(this.menu.isMirroredSchematic);
					settings.setRotation(Rotation.NONE);
					settings.setPlaced(false);
					settings.applyTo(blueprint);
					this.setItem(i, blueprint.copy());
				}
				else
				{
					if(!exceptions.contains(mb))
					{
						exceptions.add(mb);
						IGLib.IG_LOGGER.warn("An IMultiblock for the Schematic Table was not an instance of TemplateMultiblock [{}]", mb.getUniqueName());
					}
				}
			}
		}
	}

	private NonNullList<ItemStack> consumePaper(NonNullList<ItemStack> query, int crafted)
	{
		if(!query.isEmpty() && query.get(0).getCount() > 0)
		{
			ItemStack paper = query.get(0);
			if(paper.is(Items.PAPER))
			{
				int count = paper.getCount() - crafted;
				if(0 > count) query.set(0, ItemStack.EMPTY);
				if(count > 0){
					paper.setCount(count);
					query.set(0, paper.copy());
				}
			} else
			{
				IGLib.IG_LOGGER.warn("Schematic Table had an input that was not paper?");
			}
		}

		return query;
	}

	public void reduceIputs(Container inputInventory, ItemStack taken)
	{
		//Get input items
		NonNullList<ItemStack> inputs = NonNullList.withSize(inputInventory.getContainerSize()-1, ItemStack.EMPTY);
		for(int i = 0; i < inputs.size(); i++)
			inputs.set(i, inputInventory.getItem(i));
		//Consume

		consumePaper(inputs, 1);

		//Update remains
		for(int i = 0; i < inputs.size(); i++)
			inputInventory.setItem(i, inputs.get(i));

		updateOutputs(inputInventory);
	}
}
