package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCalcinationProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCrystalizerProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGReductionProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGVatProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by JStocke12 on 31-03-2020
 */
public class MaterialMineralUnobtania extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "unobtania";
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
						new ElementProportion(PeriodicTableElement.UNOBTANIUM),
						new ElementProportion(PeriodicTableElement.VANADIUM),
						new ElementProportion(PeriodicTableElement.OXYGEN, 3)
				)
		);
	}

	@Override
	public boolean hasSubtype(MaterialUseType useType) {
		switch (useType)
		{
			case COMPOUND_DUST:
				return true;
		}
		return super.hasSubtype(useType);
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.EPIC;
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

	public static int baseColor = 0x999FAF;

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
		return CrystalFamily.HEXAGONAL;
	}

	@Override
	public MaterialEnum getProcessedType(){
		return MaterialEnum.Unobtainium;
	}

	@Override
	public boolean hasSlag() {
		return true;
	}

	//Input the processing steps for this material
	@Override
	public IGMaterialProcess getProcessingMethod() {
		IGVatProcessingMethod part1 = new IGVatProcessingMethod(2000, 480);
		part1.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.DUST)));
		part1.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.COMPOUND_DUST)));
		part1.addPrimaryFluidInput(FluidEnum.SodiumHydroxide, 125);
		part1.addSecondaryFluidInput(Fluids.WATER, 125);
		part1.addFluidOutput(Fluids.EMPTY, 0);

		IGReductionProcessingMethod part2 = new IGReductionProcessingMethod(20000, 80);
		part2.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.COMPOUND_DUST)));
		part2.addItemSlag(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.SLAG)));
		part2.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanadium.getMaterial(), MaterialUseType.COMPOUND_DUST)));

		IGVatProcessingMethod part3 = new IGVatProcessingMethod(2000, 480);
		part3.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.SLAG)));
		part3.addItemOutput(ItemStack.EMPTY);
		part3.addPrimaryFluidInput(FluidEnum.SulfuricAcid, 125);
		part3.addSecondaryFluidInput(Fluids.WATER, 125);
		part3.addFluidOutput(SlurryEnum.UNOBTANIA, 0, 125);

		IGVatProcessingMethod part4 = new IGVatProcessingMethod(2000, 480);
		part4.addItemInput(ItemStack.EMPTY);
		part4.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Unobtainium.getMaterial(), MaterialUseType.COMPOUND_DUST)));
		part4.addPrimaryFluidInput(SlurryEnum.UNOBTANIA, 0, 125);
		part4.addSecondaryFluidInput(FluidEnum.Brine, 125);
		part4.addFluidOutput(FluidEnum.SulfuricAcid, 125);

		IGCalcinationProcessingMethod part5 = new IGCalcinationProcessingMethod(1000, 240);
		part5.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Unobtainium.getMaterial(), MaterialUseType.COMPOUND_DUST)));
		part5.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Unobtainium.getMaterial(), MaterialUseType.METAL_OXIDE)));

		IGVatProcessingMethod part6 = new IGVatProcessingMethod(1000, 240);
		part6.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Unobtainium.getMaterial(), MaterialUseType.METAL_OXIDE)));
		part6.addItemOutput(ItemStack.EMPTY);
		part6.addPrimaryFluidInput(FluidEnum.NitricAcid, 125);
		part6.addSecondaryFluidInput(Fluids.WATER, 125);
		part6.addFluidOutput(SlurryEnum.UNOBTANIUM, 0, 125);

		IGVatProcessingMethod part7 = new IGVatProcessingMethod(1000, 240);
		part7.addItemInput(ItemStack.EMPTY);
		part7.addItemOutput(ItemStack.EMPTY);
		part7.addPrimaryFluidInput(SlurryEnum.UNOBTANIUM, 0, 125);
		part7.addSecondaryFluidInput(FluidEnum.HydrochloricAcid, 125);
		part7.addFluidOutput(SlurryEnum.UNOBTANIUM, 1, 125);

		IGCrystalizerProcessingMethod part8 = new IGCrystalizerProcessingMethod(4000, 480);
		part8.addFluidInput(SlurryEnum.UNOBTANIUM, 1, 125);
		part8.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Unobtainium.getMaterial(), MaterialUseType.RAW_CRYSTAL)));

		inheritedProcessingMethods.add(part1);
		inheritedProcessingMethods.add(part2);
		inheritedProcessingMethods.add(part3);
		inheritedProcessingMethods.add(part4);
		inheritedProcessingMethods.add(part5);
		inheritedProcessingMethods.add(part6);
		inheritedProcessingMethods.add(part7);
		inheritedProcessingMethods.add(part8);

		return super.getProcessingMethod();
	}
}
