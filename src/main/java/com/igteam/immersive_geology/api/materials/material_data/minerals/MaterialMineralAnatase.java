package com.igteam.immersive_geology.api.materials.material_data.minerals;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGAcidProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.api.materials.material_data.fluids.slurry.MaterialSlurryWrapper;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import javafx.util.Pair;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

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
	public MaterialFluidBase[] getFluidsForSlurries(){
		return new MaterialFluidBase[]{(MaterialFluidBase) MaterialEnum.HydrochloricAcid.getMaterial()};
	}

	@Override
	public MaterialEnum getProcessedType(){
		return MaterialEnum.Titanium;
	}

	@Override
	public IGMaterialProcess getProcessingMethod() {

		Pair<FluidStack, FluidStack> inputFluids = new Pair<FluidStack, FluidStack>(new FluidStack(IGRegistrationHolder.getFluidByMaterial(getFluidsForSlurries()[0], false),125),
																new FluidStack(Fluids.WATER, 125));
		//Next step for turning Crushed Ore to Slurry
		IGAcidProcessingMethod crushedOreProcess = new IGAcidProcessingMethod(
				new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CRUSHED_ORE)),
				inputFluids,
				ItemStack.EMPTY,
				new Pair<MaterialUseType, Material>(MaterialUseType.SLURRY,this), 125, 1000, 120);

		IGAcidProcessingMethod dustProcessing = new IGAcidProcessingMethod(
				new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.RockSalt.getMaterial(), MaterialUseType.DUST), 4),
				new Pair<FluidStack, FluidStack>(new FluidStack(IGRegistrationHolder.getSlurryByMaterials(this,getFluidsForSlurries()[0], false), 125), FluidStack.EMPTY),
				new ItemStack(IGRegistrationHolder.getItemByMaterial(this.getProcessedType().getMaterial(), MaterialUseType.DUST)),
				new Pair<MaterialUseType, Material>(MaterialUseType.FLUIDS,MaterialEnum.Brine.getMaterial()), 125, 1000, 120);

		return new IGMaterialProcess(crushedOreProcess, dustProcessing);
	}

}
