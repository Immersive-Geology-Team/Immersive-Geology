package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCrystalizerProcessingMethod;
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
public class MaterialMineralPyrolusite extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "pyrolusite";
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
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2)
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
		return 4673;
	}

	@Override
	public int getMeltingPoint()
	{
		return 3663;
	}

	@Override
	public EnumMineralType getMineralType()
	{
		return EnumMineralType.CRYSTAL;
	}

	public static int baseColor = 0xc68f39;

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
		return CrystalFamily.TETRAGONAL;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return MaterialEnum.Manganese;
	}


	@Override
	public IGMaterialProcess getProcessingMethod() {
		IGVatProcessingMethod manganese_slurry_method = new IGVatProcessingMethod(1000, 120);
		manganese_slurry_method.addItemOutput(ItemStack.EMPTY);
		manganese_slurry_method.addFluidOutput(SlurryEnum.MANGANESE, 1, 125);
		manganese_slurry_method.addPrimaryFluidInput(FluidEnum.SulfuricAcid, 125);
		manganese_slurry_method.addSecondaryFluidInput(Fluids.WATER, 125);
		manganese_slurry_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CRUSHED_ORE), 1));

		IGCrystalizerProcessingMethod crystal_method; /*name of the game*/
		crystal_method = new IGCrystalizerProcessingMethod(1000, 120);
		crystal_method.addFluidInput(SlurryEnum.MANGANESE, 1, 125);
		crystal_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Manganese.getMaterial(),
				MaterialUseType.RAW_CRYSTAL), 1));

		inheritedProcessingMethods.add(manganese_slurry_method);
		inheritedProcessingMethods.add(crystal_method);
		return super.getProcessingMethod();

	}
}