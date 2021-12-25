package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
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
public class MaterialMineralAlumina extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "alumina";
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
						new ElementProportion(PeriodicTableElement.ALUMINIUM, 2),
						new ElementProportion(PeriodicTableElement.OXYGEN, 3)
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
		return MaterialEnum.Aluminium;
	}

	//Input the processing steps for this material
	@Override
	public IGMaterialProcess getProcessingMethod() {

		IGVatProcessingMethod aluminate_method = new IGVatProcessingMethod(1000, 240);
		aluminate_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.DUST)));
		aluminate_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Aluminium.getMaterial(),
				MaterialUseType.COMPOUND_DUST)));
		aluminate_method.addPrimaryFluidInput(FluidEnum.SodiumHydroxide, 125);
		aluminate_method.addSecondaryFluidInput(Fluids.WATER, 125);
		aluminate_method.addFluidOutput(Fluids.EMPTY, 0);

		IGVatProcessingMethod cryolite_method = new IGVatProcessingMethod(1000, 120);
		cryolite_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Aluminium.getMaterial(),
				MaterialUseType.COMPOUND_DUST)));
		cryolite_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Cryolite.getMaterial(),
				MaterialUseType.DUST)));
		cryolite_method.addFluidOutput(Fluids.EMPTY, 0);
		cryolite_method.addSecondaryFluidInput(Fluids.WATER, 125);
		cryolite_method.addPrimaryFluidInput(FluidEnum.HydrofluoricAcid, 125);


		//TODO -- Arc Furnace method of cryolite, alumina and cryolite to cryolite + Al ingots

		inheritedProcessingMethods.add(cryolite_method);
		inheritedProcessingMethods.add(aluminate_method);

		return super.getProcessingMethod();
	}
}
