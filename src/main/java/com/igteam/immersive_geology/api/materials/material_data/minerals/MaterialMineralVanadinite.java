package com.igteam.immersive_geology.api.materials.material_data.minerals;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
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
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralVanadinite extends MaterialMineralBase
{

	@Override
	public String getName()
	{
		return "vanadinite";
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
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.LEAD, 5),
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.VANADIUM),
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4),
						new PeriodicTableElement.ElementProportion(PeriodicTableElement.CHLORINE)
				)
		);
	}

	@Override
	public Rarity getRarity()
	{
		// TODO Auto-generated method stub
		return Rarity.COMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		// TODO Auto-generated method stub
		return 7835;
	}

	@Override
	public int getMeltingPoint()
	{
		// TODO Auto-generated method stub
		return 3470;
	}

	public static int baseColor = 0xEF2161;

	@Override
	public int getColor(int temperature)
	{
		// TODO Auto-generated method stub
		return baseColor;
	}

	@Override
	public float getHardness()
	{
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public float getMiningResistance()
	{
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public float getBlastResistance()
	{
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public float getDensity()
	{
		// TODO Auto-generated method stub
		return 8.96f;
	}

	@Override
	public EnumMineralType getMineralType()
	{
		// TODO Auto-generated method stub
		return EnumMineralType.CRYSTAL;
	}

	@Override
	public CrystalFamily getCrystalFamily() {
		return CrystalFamily.HEXAGONAL;
	}


	@Override
	public MaterialEnum getProcessedType() {
		return MaterialEnum.Vanadium;
	}

	@Override
	public MaterialEnum getSecondaryType() {
		return MaterialEnum.Lead;
	}

	@Override
	public boolean hasSlag() {return true;}

	@Override
	public IGMaterialProcess getProcessingMethod() {

		IGReductionProcessingMethod redox_method = new IGReductionProcessingMethod(1000, 240);
		redox_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CRUSHED_ORE), 2));
		redox_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Lead.getMaterial(), MaterialUseType.INGOT), 1));
		redox_method.addItemSlag(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.SLAG), 1));

		//small simplification here

		IGVatProcessingMethod slurry_method = new IGVatProcessingMethod(1000, 240);
		slurry_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.SLAG), 1));
		slurry_method.addPrimaryFluidInput(FluidEnum.SulfuricAcid,125);
		slurry_method.addSecondaryFluidInput(Fluids.WATER, 125);
		slurry_method.addFluidOutput(SlurryEnum.VANADIUM,0,125);
		slurry_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanadium.getMaterial(),
				MaterialUseType.COMPOUND_DUST), 1));


		//TODO -- add roasting process to compound dust
		IGCalcinationProcessingMethod calcination_method = new IGCalcinationProcessingMethod(1000, 240);
		calcination_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanadium.getMaterial(),
				MaterialUseType.COMPOUND_DUST), 1));
		calcination_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanadium.getMaterial(),
				MaterialUseType.METAL_OXIDE), 1));


		//TODO -- add Arc furnacing of Vanadium oxide

		IGArcFurnaceProcessingMethod smelting_method = new IGArcFurnaceProcessingMethod(20000,80,
				new IngredientWithSize(IETags.coalCokeDust,1));
		//REFACTOR -- chance based shenanigans here
		smelting_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this.getProcessedType().getMaterial(),
				MaterialUseType.METAL_OXIDE)));
		smelting_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanadium.getMaterial(),
				MaterialUseType.INGOT)));


		inheritedProcessingMethods.add(calcination_method);
		inheritedProcessingMethods.add(smelting_method);
		inheritedProcessingMethods.add(redox_method);
		inheritedProcessingMethods.add(slurry_method);

		return super.getProcessingMethod();
	}
}
