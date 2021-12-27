package com.igteam.immersive_geology.api.materials.material_data.metals;

import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by JStocke12 on 27-03-2020.
 */
public class MaterialMetalThorium extends MaterialMetalBase
{
	@Override
	public String getName()
	{
		return "thorium";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return IGLib.MODID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
				new ElementProportion(PeriodicTableElement.THORIUM)
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
		return 5061;
	}

	@Override
	public int getMeltingPoint()
	{
		return 2023;
	}

	@Override
	public int getColor(int temperature)
	{
		return 0x45484b;
	}

	//Needs to be changed in code for subtypes, such as sheetmetal
	@Override
	public float getHardness()
	{
		return 10.0F;
	}

	@Override
	public float getMiningResistance()
	{
		return 20.0F;
	}

	@Override
	public float getBlastResistance()
	{
		return 10;
	}

	//Copied from Immersive Intelligence (steel has i think 1.65, leaves 0.35)
	@Override
	public float getDensity()
	{
		return 11.7f;
	}

	//Stone pickaxe level
	@Override
	public int getBlockHarvestLevel()
	{
		return 3;
	}

	@Override
	public EnumMetalType getMetalType()
	{
		return EnumMetalType.METAL;
	}

	@Override
	public CrystalFamily getCrystalFamily() {
		return CrystalFamily.CUBIC;
	}

	@Override
	public boolean hasGear() {
		return false;
	}

	@Override
	public boolean hasWire() {
		return false;
	}

	@Override
	public boolean hasPlate() {
		return false;
	}

	@Override
	public boolean hasSheetmetal() {
		return false;
	}

	/*@Nullable
	@Override
	public IItemTier getToolTier()
	{
		return IGContent.;
	}*/
}
