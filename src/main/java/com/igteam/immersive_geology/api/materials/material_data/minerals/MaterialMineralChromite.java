package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
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
public class MaterialMineralChromite extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "chromite";
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
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.CHROMIUM, 2),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
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

	public static int baseColor = 0x615964;

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

	/*@Nullable
	@Override
	public IItemTier getToolTier()
	{
		return IGContent.;
	}*/


	public MaterialEnum getProcessedType(){
		return MaterialEnum.Chromium;
	}


	@Override
	public MaterialEnum getSecondaryType() {
		return MaterialEnum.Iron;
	}

	@Override
	public IGMaterialProcess getProcessingMethod() {


		IGVatProcessingMethod slurry_method = new IGVatProcessingMethod(1000, 240);
		slurry_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CRUSHED_ORE), 1));
		slurry_method.addPrimaryFluidInput(FluidEnum.NitricAcid,125);
		slurry_method.addSecondaryFluidInput(Fluids.WATER, 125);
		slurry_method.addFluidOutput(SlurryEnum.CHROMIUM,1,125);
		slurry_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Iron.getMaterial(),
				MaterialUseType.COMPOUND_DUST), 1));

		IGCalcinationProcessingMethod calcination_method = new IGCalcinationProcessingMethod(1000, 240);
		calcination_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Iron.getMaterial(),
				MaterialUseType.COMPOUND_DUST), 1));
		calcination_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Hematite.getMaterial(),
				MaterialUseType.CRUSHED_ORE), 1));
		//TODO -- add roasting process of iron nitrate
		IGVatProcessingMethod slurry_method_pt2 = new IGVatProcessingMethod(1000, 240);
		slurry_method_pt2.addItemInput(ItemStack.EMPTY);
		slurry_method_pt2.addPrimaryFluidInput(FluidEnum.HydrochloricAcid,125);
		slurry_method_pt2.addSecondaryFluidInput(SlurryEnum.CHROMIUM,1, 125);
		slurry_method_pt2.addFluidOutput(SlurryEnum.CHROMIUM,0,125);
		slurry_method_pt2.addItemOutput(ItemStack.EMPTY);

		IGCrystalizerProcessingMethod crystalizerMethod = new IGCrystalizerProcessingMethod(1000, 120);
		crystalizerMethod.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Chromium.getMaterial(),
				MaterialUseType.RAW_CRYSTAL),1));
		crystalizerMethod.addFluidInput(SlurryEnum.CHROMIUM,0,125);
		inheritedProcessingMethods.add(calcination_method);

		inheritedProcessingMethods.add(crystalizerMethod);
		inheritedProcessingMethods.add(slurry_method);
		inheritedProcessingMethods.add(slurry_method_pt2);

		return super.getProcessingMethod();
	}
}
