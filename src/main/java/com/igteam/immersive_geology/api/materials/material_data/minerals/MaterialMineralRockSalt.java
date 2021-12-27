package com.igteam.immersive_geology.api.materials.material_data.minerals;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGArcFurnaceProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCalcinationProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGReductionProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGVatProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import org.lwjgl.system.CallbackI;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by JStocke12 on 31-03-2020
 */
public class MaterialMineralRockSalt extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "rocksalt";
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
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.SODIUM),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.CHLORINE)
		)
		);
	}

	@Override
	public boolean hasSubtype(MaterialUseType useType) {
		if(useType == MaterialUseType.DUST){
			return true;
		}
		return super.hasSubtype(useType);
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


	public static int baseColor = 0xffffff;


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
	public IGMaterialProcess getProcessingMethod() {
		IGCalcinationProcessingMethod method = new IGCalcinationProcessingMethod(1000, 240);
		method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Sodium.getMaterial(),
				MaterialUseType.COMPOUND_DUST)));

		method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Sodium.getMaterial(),
				MaterialUseType.METAL_OXIDE)));

		IGVatProcessingMethod sodium_method = new IGVatProcessingMethod(1000, 120);
		sodium_method.addFluidOutput(FluidEnum.SodiumHydroxide,125);
		sodium_method.addItemOutput(ItemStack.EMPTY);
		sodium_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Sodium.getMaterial(),
				MaterialUseType.METAL_OXIDE)));
		sodium_method.addPrimaryFluidInput(Fluids.WATER, 125);
		sodium_method.addSecondaryFluidInput(Fluids.EMPTY, 0);

		IGArcFurnaceProcessingMethod arc_method = new IGArcFurnaceProcessingMethod(2000, 120, new IngredientWithSize(IETags.coalCokeDust,1));
		arc_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Sodium.getMaterial(), MaterialUseType.DUST)));
		arc_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Sodium.getMaterial(),
				MaterialUseType.METAL_OXIDE)));

		inheritedProcessingMethods.add(arc_method);
		inheritedProcessingMethods.add(sodium_method);
		inheritedProcessingMethods.add(method);

		return super.getProcessingMethod();
	}
	/*@Nullable
	@Override
	public IItemTier getToolTier()
	{
		return IGContent.;
	}*/
}
