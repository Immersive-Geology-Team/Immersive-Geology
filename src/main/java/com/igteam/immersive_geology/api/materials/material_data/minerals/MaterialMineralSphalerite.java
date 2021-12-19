package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
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

public class MaterialMineralSphalerite extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "sphalerite";
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
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.ZINC),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR)
		));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.COMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		return 2400;
	}

	@Override
	public int getMeltingPoint()
	{
		return 1100;
	}

	@Override
	public EnumMineralType getMineralType()
	{
		return EnumMineralType.CRYSTAL;
	}

	public static int baseColor = 0x6F8070;

	@Override
	public int getColor(int temperature)
	{
		return baseColor;
	}

	@Override
	public float getHardness()
	{
		return 1;
	}

	@Override
	public float getMiningResistance()
	{
		return 1;
	}

	@Override
	public float getBlastResistance()
	{
		return 1;
	}

	@Override
	public float getDensity()
	{
		return 0;
	}

	@Override
	public int getBlockHarvestLevel()
	{
		return 0;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return MaterialEnum.Zinc;
	}

	@Override
	public MaterialEnum getSecondaryType() {
		return MaterialEnum.Iron;
	}

	@Override
	public boolean hasSlag() {return true;}

	@Override
	public IGMaterialProcess getProcessingMethod() {
		//TODO -- add roasting process
		IGVatProcessingMethod slurry_method = new IGVatProcessingMethod(1000, 240);
		slurry_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.SLAG), 1));
		slurry_method.addPrimaryFluidInput(FluidEnum.SulfuricAcid,125);
		slurry_method.addSecondaryFluidInput(Fluids.WATER, 125);
		slurry_method.addFluidOutput(SlurryEnum.ZINC,1,125);
		slurry_method.addItemOutput(ItemStack.EMPTY);

		//TODO -- add crystallization process

		return new IGMaterialProcess(slurry_method);
	}
}
