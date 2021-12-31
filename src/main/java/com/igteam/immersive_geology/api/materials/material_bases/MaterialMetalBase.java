package com.igteam.immersive_geology.api.materials.material_bases;

import blusunrize.immersiveengineering.common.items.IEItems;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.MaterialTypes;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGArcFurnaceProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGBloomeryProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCraftingProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCrushingProcessingMethod;
import com.igteam.immersive_geology.api.tags.IGTags;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public abstract class MaterialMetalBase extends Material
{
	public abstract EnumMetalType getMetalType();

	@Override
	public boolean hasSubtype(MaterialUseType useType)
	{
		switch(useType)
		{
			case INGOT:
				return hasIngot();
			case NUGGET:
				return hasNugget();
			case PLATE:
				return hasPlate();
			case ROD:
				return hasRod();
			case GEAR:
				return hasGear();
			case WIRE:
				return hasWire();
			case COMPOUND_DUST:
				return hasCompoundDust();
			case DUST:
			case TINY_DUST:
				return hasDust();
			case SHEETMETAL:
				return hasSheetmetal();
			case SLAB:
				return hasSlab();
			case SHEETMETAL_STAIRS:
				return hasStairs();
			case DUST_BLOCK:
				return hasDustBlock();
			case STORAGE_BLOCK:
				return true;
			case METAL_OXIDE:
				return generateOxide();
			case RAW_CRYSTAL:
				return hasCrystal();
			case ORE_CHUNK:
			case ORE_BIT:
			case DIRTY_CRUSHED_ORE:
			case CRUSHED_ORE:
			case ORE_STONE:
				return isNativeMetal();
			default:
				return false;
		}
	}

	/**
	 * Use it for chemical waste stuff
	 * @return
	 */
	public boolean hasCompoundDust() {
		return false;
	}

	public boolean generateOxide(){
		return false;// !(isAlloy() || isNativeMetal);
	}

	public boolean isAlloy(){
		return getMetalType() == EnumMetalType.ALLOY;
	}

	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.METAL;
	}
	
	@Override
	public MaterialTypes getMaterialSubType()
	{
		return (isNativeMetal() ? MaterialTypes.MINERAL : MaterialTypes.METAL);
	}

	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.IRON;
	}

	public enum EnumMetalType
	{
		METALLOID,
		METAL,
		ALLOY
	}

	public boolean hasIngot()
	{
		return true;
	}

	public boolean hasNugget()
	{
		return true;
	}

	public boolean hasPlate()
	{
		return true;
	}

	public boolean hasRod()
	{
		return true;
	}

	public boolean hasGear()
	{
		return true;
	}

	public boolean hasWire()
	{
		return true;
	}

	public boolean hasSheetmetal()
	{
		return true;
	}

	public boolean hasSlab()
	{
		return true;
	}

	public boolean hasStairs() { return true; }

	public boolean hasDust()
	{
		return true;
	}

	public boolean hasDustBlock()
	{
		return true;
	}

	public boolean hasCrystal(){
		return true;
	}

	public boolean isNativeMetal() {return false;}

	@Override
	public MaterialEnum getProcessedType() {
		return null;
	}


	@Override
	public MaterialEnum getSecondaryType() {
		return null;
	}

	@Override
	public boolean preExists() {
		return false;
	}

	//Input the processing steps for this material
	@Override
	public IGMaterialProcess getProcessingMethod() {
		if(isNativeMetal()) {
			Item inputCrushManual = IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanilla.getMaterial(), this, MaterialUseType.ORE_CHUNK);

			IGCraftingProcessingMethod defaultNativeOreCrushing = new IGCraftingProcessingMethod("has_ore_chunk", IGTags.getTagsFor(this).ore_crushed);
			defaultNativeOreCrushing.setShapeless(inputCrushManual, inputCrushManual);
			defaultNativeOreCrushing.setOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanilla.getMaterial(), this, MaterialUseType.DIRTY_CRUSHED_ORE), 1));

			Item inputDirtyCrush = IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanilla.getMaterial(), this, MaterialUseType.DIRTY_CRUSHED_ORE);
			IGCraftingProcessingMethod manualClean = new IGCraftingProcessingMethod("has_ore_chunk", IGTags.getTagsFor(this).ore_crushed);
			manualClean.setShapeless(inputDirtyCrush, inputDirtyCrush);
			manualClean.setOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial( this, MaterialUseType.CRUSHED_ORE), 1));

			IGBloomeryProcessingMethod bloomeryNativeIngot = new IGBloomeryProcessingMethod(10, 1);
			bloomeryNativeIngot.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CRUSHED_ORE), 2));
			bloomeryNativeIngot.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.INGOT)));



			inheritedProcessingMethods.add(bloomeryNativeIngot);
			inheritedProcessingMethods.add(defaultNativeOreCrushing);
			inheritedProcessingMethods.add(manualClean);
		}

		if(hasCrystal()) {
			if (hasDust()) {
				IGCrushingProcessingMethod toDust = new IGCrushingProcessingMethod(1000, 100);
				toDust.inputItem(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.RAW_CRYSTAL)));
				toDust.outputItem(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.DUST)));
				toDust.secondaryItem(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.TINY_DUST)));
				inheritedProcessingMethods.add(toDust);
			}
			if (hasIngot()) {
				IGArcFurnaceProcessingMethod crystalToIngot = new IGArcFurnaceProcessingMethod(250, 100);
				crystalToIngot.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.RAW_CRYSTAL)));
				crystalToIngot.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.INGOT)));
				crystalToIngot.addItemSlag(new ItemStack(IEItems.Ingredients.slag));
			}
		}

		if(hasSubtype(MaterialUseType.DUST) && hasSubtype(MaterialUseType.INGOT)) {
			IGArcFurnaceProcessingMethod gritToIngot = new IGArcFurnaceProcessingMethod(250, 100);
			gritToIngot.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.DUST)));
			gritToIngot.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.INGOT)));
			gritToIngot.addItemSlag(new ItemStack(IEItems.Ingredients.slag));

			inheritedProcessingMethods.add(gritToIngot);
		}

		if(hasSubtype(MaterialUseType.PLATE) && hasSubtype(MaterialUseType.INGOT)) {
			IGCraftingProcessingMethod ingotToPlate = new IGCraftingProcessingMethod("ingot_to_plate", IGTags.getTagsFor(this).ingot);
			ingotToPlate.setShapeless(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.INGOT), IEItems.Tools.hammer);
			ingotToPlate.setOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.PLATE)));

			inheritedProcessingMethods.add(ingotToPlate);
		}

		return super.getProcessingMethod();
	}

}
