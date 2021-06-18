package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.CrystalFamily;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralCassiterite extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "cassiterite";
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
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.TIN),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2)
		));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.UNCOMMON;
	}

	@Override
	public int getBoilingPoint()
	{
		return 2073;
	}

	@Override
	public int getMeltingPoint()
	{
		return 1903;
	}

	@Override
	public EnumMineralType getMineralType()
	{
		return EnumMineralType.CRYSTAL;
	}

	public static int baseColor = 0x8f8b96;

	@Override
	public int getColor(int temperature)
	{
		return 0x3B3B3D;
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
		return MaterialEnum.Tin;
	}
}