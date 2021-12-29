package com.igteam.immersive_geology.api.materials.material_bases;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.MaterialTypes;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCraftingProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCrushingProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase.EnumStoneType;
import com.igteam.immersive_geology.api.tags.IGTags;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public abstract class MaterialMineralBase extends Material
{

	public MaterialMineralBase() {
	}

	public abstract EnumMineralType getMineralType();

	@Override
	public boolean hasSubtype(MaterialUseType useType)
	{
		switch(useType)
		{
			case CRUSHED_ORE:
			case DIRTY_CRUSHED_ORE:
				return hasCrushedOre();
			case ORE_STONE:
				return true;
			case ORE_BIT:
			case ORE_CHUNK:
				return hasOreChunks();
			case TINY_DUST:
			case DUST:
				return hasDust();
			case CLAY:
				return hasClay();
			case SLAG:
				return hasSlag();
			case FUEL:
				return hasFuel();

		}
		return false;
	}

	public boolean hasSlag() {
		return false;
	}

	public boolean hasOreChunks() { return getMineralType() != EnumMineralType.CLAY; }

	public boolean hasCrushedOre(){ return hasOreChunks(); }

	public boolean hasDust() { return getMineralType() != EnumMineralType.CLAY; }

	public boolean hasClay(){
		return getMineralType() == EnumMineralType.CLAY;
	}

	public boolean hasFuel(){
		return getMineralType() == EnumMineralType.FUEL;
	}

	public boolean isOxide(){
		boolean result = false;

		int element_length = getElements().size();

		if(element_length == 2){
			for(PeriodicTableElement.ElementProportion e : getElements()){
				if(e.getElement().getSymbol().equals(PeriodicTableElement.OXYGEN.getSymbol())) {
					result = true;
				}
			}
		}

		return result;
	}

	@Nullable
	@Override
	public String getSpecialSubtypeModelName(MaterialUseType useType)
	{
		if(useType==MaterialUseType.ORE_STONE)
			return EnumStoneType.SEDIMENTARY.getName();
		return null;
	}

	protected boolean hasCrystal(){
		return getMineralType() == EnumMineralType.CRYSTAL;
	}

	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.MINERAL;
	}

	@Override
	public MaterialTypes getMaterialSubType()
	{
		return MaterialTypes.MINERAL;
	}
	
	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return net.minecraft.block.material.Material.ROCK;
	}

	public enum EnumMineralType
	{
		NATIVE,
		CRYSTAL,
		FUEL,
		CLAY,
		NONE
	}

	public int getStaticColor()
	{
		return 0xffffff;
	}

	//Defaults to Iron
	@Override
	public MaterialEnum getProcessedType(){
		return MaterialEnum.Iron;
	}

	@Override
	public boolean preExists() {
		return false;
	}

	@Override
	public MaterialEnum getSecondaryType() {
		return null;
	}

	@Override
	public IGMaterialProcess getProcessingMethod() {
		if(hasCrushedOre()) {
			Item inputCrushManual = IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanilla.getMaterial(), this, MaterialUseType.ORE_CHUNK);
			IGCraftingProcessingMethod defaultNativeOreCrushing = new IGCraftingProcessingMethod("has_ore_chunk", IGTags.getTagsFor(this).ore_crushed);
			defaultNativeOreCrushing.setShapeless(inputCrushManual, inputCrushManual);
			defaultNativeOreCrushing.setOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanilla.getMaterial(), this, MaterialUseType.DIRTY_CRUSHED_ORE), 1));

			Item inputDirtyCrush = IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanilla.getMaterial(), this, MaterialUseType.DIRTY_CRUSHED_ORE);
			IGCraftingProcessingMethod manualClean = new IGCraftingProcessingMethod("has_ore_chunk", IGTags.getTagsFor(this).ore_crushed);
			manualClean.setShapeless(inputDirtyCrush, inputDirtyCrush);
			manualClean.setOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CRUSHED_ORE), 1));


			IGCrushingProcessingMethod ore2Dust = new IGCrushingProcessingMethod(1000, 80);
			ore2Dust.inputItem(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CRUSHED_ORE), 1));
			ore2Dust.outputItem(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.DUST), 1));

			inheritedProcessingMethods.add(ore2Dust);
			inheritedProcessingMethods.add(defaultNativeOreCrushing);
			inheritedProcessingMethods.add(manualClean);
		}

		return super.getProcessingMethod();
	}
}
