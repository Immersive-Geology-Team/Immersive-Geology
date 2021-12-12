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
public class MaterialMetalSodium extends MaterialMetalBase
{
	@Override
	public String getName()
	{
		return "sodium";
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
				new ElementProportion(PeriodicTableElement.SODIUM)
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
		return 1156;
	}

	@Override
	public int getMeltingPoint()
	{
		return 370;
	}

	@Override
	public int getColor(int temperature)
	{
		return 0xd0d5db;
	}

	//Needs to be changed in code for subtypes, such as sheetmetal
	@Override
	public float getHardness()
	{
		return 5.0F;
	}

	@Override
	public float getMiningResistance()
	{
		return 10.0F;
	}

	@Override
	public float getBlastResistance()
	{
		return 6;
	}

	//Copied from Immersive Intelligence (steel has i think 1.65, leaves 0.35)
	@Override
	public float getDensity()
	{
		return 0.968f;
	}

	//Stone pickaxe level
	@Override
	public int getBlockHarvestLevel()
	{
		return 1;
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

	public boolean hasIngot()
	{
		return true;
	}

	public boolean hasNugget()
	{
		return true;
	}

	public boolean hasPlate()
	{
		return false;
	}

	public boolean hasRod()
	{
		return false;
	}

	public boolean hasGear()
	{
		return false;
	}

	public boolean hasWire() {return false;}

	public boolean hasSheetmetal()
	{
		return false;
	}

	public boolean hasSlab()
	{
		return false;
	}

	public boolean hasStairs() { return false; }

	public boolean hasDust()
	{
		return true;
	}

	public boolean hasDustBlock()
	{
		return false;
	}

	public boolean hasCrystal(){
		return true;
	}
}
