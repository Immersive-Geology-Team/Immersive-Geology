package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
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

public class MaterialMineralHubnerite extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "hubnerite";
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
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.MANGANESE),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.TUNGSTEN),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
		));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.RARE;
	}

	@Override
	public int getBoilingPoint()
	{
		return 6202;
	}

	@Override
	public int getMeltingPoint()
	{
		return 3695;
	}

	@Override
	public EnumMineralType getMineralType()
	{
		return EnumMineralType.CRYSTAL;
	}


	public static int baseColor = 0x32332E;

	@Override
	public int getColor(int temperature)
	{
		return baseColor;
	}

	@Override
	public float getHardness()
	{
		return 0;
	}

	@Override
	public float getMiningResistance()
	{
		return 0;
	}

	@Override
	public float getBlastResistance()
	{
		return 0;
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
	public CrystalFamily getCrystalFamily() {
		return CrystalFamily.MONOCLINIC;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return MaterialEnum.Tungsten;
	}

	@Override
	public MaterialEnum getSecondaryType() {
		return MaterialEnum.Manganese;
	}


	//TODO refactor to include crusher stuff as well!
	//Input the processing steps for this material
	@Override
	public IGMaterialProcess getProcessingMethod() {
		IGVatProcessingMethod manganese_slurry_method = new IGVatProcessingMethod(1000, 120);
		manganese_slurry_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Tungsten.getMaterial(),
				MaterialUseType.COMPOUND_DUST), 1));
		manganese_slurry_method.addFluidOutput(SlurryEnum.MANGANESE, 0, 125);
		manganese_slurry_method.addPrimaryFluidInput(FluidEnum.HydrochloricAcid, 125);
		manganese_slurry_method.addSecondaryFluidInput(Fluids.WATER, 125);
		manganese_slurry_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.DUST), 1));

		IGCalcinationProcessingMethod calcination_method = new IGCalcinationProcessingMethod(1000, 240);
		calcination_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Tungsten.getMaterial(),
				MaterialUseType.COMPOUND_DUST), 1));
		calcination_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Tungsten.getMaterial(),
				MaterialUseType.METAL_OXIDE), 1));

		IGReductionProcessingMethod reduction_method = new IGReductionProcessingMethod(1000, 240);
		reduction_method.addItemSlag(ItemStack.EMPTY);
		reduction_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Tungsten.getMaterial(),
				MaterialUseType.METAL_OXIDE), 1));
		reduction_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Tungsten.getMaterial(),
				MaterialUseType.DUST), 1));

		IGVatProcessingMethod manganese_sulf_slurry_method = new IGVatProcessingMethod(1000, 120);
		manganese_sulf_slurry_method.addItemOutput(ItemStack.EMPTY);
		manganese_sulf_slurry_method.addFluidOutput(SlurryEnum.MANGANESE, 1, 125);
		manganese_sulf_slurry_method.addPrimaryFluidInput(FluidEnum.SulfuricAcid, 125);
		manganese_sulf_slurry_method.addSecondaryFluidInput(SlurryEnum.MANGANESE, 0, 125);
		manganese_sulf_slurry_method.addItemInput(ItemStack.EMPTY);


		IGCrystalizerProcessingMethod crystal_method; /*name of the game*/
		crystal_method = new IGCrystalizerProcessingMethod(1000, 120);
		crystal_method.addFluidInput(SlurryEnum.MANGANESE, 1, 125);
		crystal_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Manganese.getMaterial(),
				MaterialUseType.RAW_CRYSTAL), 1));

		inheritedProcessingMethods.add(crystal_method);
		inheritedProcessingMethods.add(manganese_slurry_method);
		inheritedProcessingMethods.add(manganese_sulf_slurry_method);
		inheritedProcessingMethods.add(calcination_method);
		inheritedProcessingMethods.add(reduction_method);

		return super.getProcessingMethod();

	}

	@Override
	public boolean hasSlurry() {
		return true;
	}
}