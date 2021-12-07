package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGVatProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by JStocke12 on 31-03-2020
 */
public class MaterialMineralAnatase extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "anatase";
	}

	@Override
	public String getModID()
	{
		return IGLib.MODID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(
				Arrays.asList(
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.TITANIUM),
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2)
				)
		);

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

	public static int baseColor = 0x475B74;

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
	public int getStaticColor()
	{
		return baseColor;
	}

	@Override
	public CrystalFamily getCrystalFamily() {
		return CrystalFamily.TETRAGONAL;
	}

	@Override
	public boolean hasSlurry() {
		return true;
	}

	@Override
	public MaterialEnum getProcessedType(){
		return MaterialEnum.Titanium;
	}

	@Override
	public IGMaterialProcess getProcessingMethod() {
		//Create Titanium Slurry
		IGVatProcessingMethod titanium_slurry_method = new IGVatProcessingMethod(1000, 120);
		titanium_slurry_method.addItemOutput(ItemStack.EMPTY);
		titanium_slurry_method.addFluidOutput(SlurryEnum.TITANIUM, 0, 125);
		titanium_slurry_method.addPrimaryFluidInput(FluidEnum.HydrochloricAcid, 125);
		titanium_slurry_method.addSecondaryFluidInput(Fluids.WATER, 125);
		titanium_slurry_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CRUSHED_ORE), 1));

		IGVatProcessingMethod sedimentary_method = new IGVatProcessingMethod(1000, 240);
		sedimentary_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Titanium.getMaterial(), MaterialUseType.DUST)));
		sedimentary_method.addFluidOutput(FluidEnum.Brine, 500);
		sedimentary_method.addPrimaryFluidInput(SlurryEnum.TITANIUM, 0, 125);
		sedimentary_method.addSecondaryFluidInput(Fluids.EMPTY, 0);
		sedimentary_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Sodium.getMaterial(), MaterialUseType.DUST), 4));

		return new IGMaterialProcess(titanium_slurry_method, sedimentary_method);
	}
}
