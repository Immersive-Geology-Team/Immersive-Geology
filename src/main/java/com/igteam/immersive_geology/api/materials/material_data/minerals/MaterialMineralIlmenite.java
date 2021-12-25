package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGReductionProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralIlmenite extends MaterialMineralBase
{

	@Override
	public String getName()
	{
		return "ilmenite";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return IGLib.MODID;
	}


	@Override
	public EnumMineralType getMineralType()
	{
		// TODO Auto-generated method stub
		return EnumMineralType.CRYSTAL;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		// TODO Auto-generated method stub
		return new LinkedHashSet<>(Arrays.asList(
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.TITANIUM),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
		));
	}

	@Override
	public Rarity getRarity()
	{
		// TODO Auto-generated method stub
		return Rarity.RARE;
	}

	@Override
	public int getBoilingPoint()
	{
		// TODO Auto-generated method stub
		return 3923;
	}

	@Override
	public int getMeltingPoint()
	{
		// TODO Auto-generated method stub
		return 1923;
	}

	public static int baseColor = 0x4A3E3E;

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
		return 1;
	}

	@Override
	public float getBlastResistance()
	{
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float getDensity()
	{
		// TODO Auto-generated method stub
		return 4.55f;
	}

	@Override
	public CrystalFamily getCrystalFamily() {
		return CrystalFamily.HEXAGONAL;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return MaterialEnum.Titanium;
	}

	@Override
	public MaterialEnum getSecondaryType() {
		return MaterialEnum.Iron;
	}

	@Override
	public IGMaterialProcess getProcessingMethod() {
		//I've put simplification here
		IGReductionProcessingMethod method = new IGReductionProcessingMethod(1000, 240);
		method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this, MaterialUseType.CRUSHED_ORE), 1));
		method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Iron.getMaterial(), MaterialUseType.INGOT)));
		method.addItemSlag(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanilla.getMaterial(),
				MaterialEnum.Anatase.getMaterial(), MaterialUseType.DIRTY_CRUSHED_ORE)));

		inheritedProcessingMethods.add(method);

		return super.getProcessingMethod();
	}
}
