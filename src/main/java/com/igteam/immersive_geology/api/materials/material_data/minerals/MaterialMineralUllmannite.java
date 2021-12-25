package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCrystalizerProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGRoastingProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGVatProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by JStocke12 on 31-03-2020
 */
public class MaterialMineralUllmannite extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "ullmannite";
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
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.NICKEL),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.ANTIMONY),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR)
		)
		);
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.UNCOMMON;
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

	public static int baseColor = 0x484742;

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
		return 6.33f;//g/cm^3
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
		return Material.IRON;
	}

	/*@Nullable
	@Override
	public IItemTier getToolTier()
	{
		return IGContent.;
	}*/

	@Override
	public MaterialEnum getProcessedType() {
		return MaterialEnum.Nickel;
	}

	@Override
	public MaterialEnum getSecondaryType() {
		return MaterialEnum.Antimony;
	}

	public IGMaterialProcess getProcessingMethod() {
		//TODO -- add roasting process

		IGRoastingProcessingMethod roasting_method = new IGRoastingProcessingMethod(100, 1);
		roasting_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this,
				MaterialUseType.CRUSHED_ORE), 1));
		roasting_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(getProcessedType().getMaterial(),
				MaterialUseType.METAL_OXIDE), 1));


		IGVatProcessingMethod slurry_method = new IGVatProcessingMethod(1000, 240);
		slurry_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Nickel.getMaterial(),
				MaterialUseType.METAL_OXIDE), 1));
		slurry_method.addPrimaryFluidInput(FluidEnum.HydrochloricAcid,125);
		slurry_method.addSecondaryFluidInput(Fluids.WATER, 125);
		slurry_method.addFluidOutput(SlurryEnum.NICKEL,0,125);
		slurry_method.addItemOutput(ItemStack.EMPTY);

		IGCrystalizerProcessingMethod crystal_method = new IGCrystalizerProcessingMethod(1000, 120);
		crystal_method.addFluidInput(SlurryEnum.NICKEL,0,125);
		crystal_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Nickel.getMaterial(),
				MaterialUseType.RAW_CRYSTAL), 1));

		inheritedProcessingMethods.add(roasting_method);

		inheritedProcessingMethods.add(slurry_method);
		inheritedProcessingMethods.add(crystal_method);
		return super.getProcessingMethod();
	}

}
