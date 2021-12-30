package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGBloomeryProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGRoastingProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGSeparationProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by JStocke12 on 31-03-2020
 */
public class MaterialMineralChalcopyrite extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "chalcopyrite";
	}

	@Override
	public String getModID()
	{
		return IGLib.MODID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.COPPER),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR, 2)
		));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.COMMON;
	}

	@Override
	public EnumMineralType getMineralType()
	{
		return EnumMineralType.CRYSTAL;
	}

	@Override
	public int getBoilingPoint()
	{
		return -1;
	}

	@Override
	public int getMeltingPoint()
	{
		return -1;
	}

	public static int baseColor = 0x5B4D2A;

	@Override
	public int getColor(int temperature)
	{
		return baseColor;
	}

	//Needs to be changed in code for subtypes, such as sheetmetal
	@Override
	public float getHardness()
	{
		return -1F;
	}

	@Override
	public float getMiningResistance()
	{
		return -1F;
	}

	@Override
	public float getBlastResistance()
	{
		return -1F;
	}

	//Copied from Immersive Intelligence (steel has i think 1.65, leaves 0.35)
	@Override
	public float getDensity()
	{
		return 1f;
	}

	//Stone pickaxe level
	@Override
	public int getBlockHarvestLevel()
	{
		return 0;
	}

	@Override
	public net.minecraft.block.material.Material getBlockMaterial()
	{
		return null;
	}

	@Override
	public CrystalFamily getCrystalFamily() {
		return CrystalFamily.TETRAGONAL;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return MaterialEnum.Copper;
	}

	@Override
	public MaterialEnum getSecondaryType() {
		return MaterialEnum.Iron;
	}

	@Override
	public boolean hasSlag() {return  true;}

	public IGMaterialProcess getProcessingMethod() {
		//A bit lazy approach
		IGRoastingProcessingMethod roasting_method = new IGRoastingProcessingMethod(100, 2);
		roasting_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this,
				MaterialUseType.CRUSHED_ORE), 1));
		roasting_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this,
				MaterialUseType.SLAG), 1));

		IGSeparationProcessingMethod sep_method = new IGSeparationProcessingMethod(120);
		sep_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this,
				MaterialUseType.SLAG), 1));
		sep_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Hematite.getMaterial(),
				MaterialUseType.CRUSHED_ORE), 1));
		sep_method.addItemWaste(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Cuprite.getMaterial(),
				MaterialUseType.CRUSHED_ORE), 1));


		inheritedProcessingMethods.add(roasting_method);
		inheritedProcessingMethods.add(sep_method);
		return super.getProcessingMethod();
	}
}
