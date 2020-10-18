package com.igteam.immersivegeology.common.materials.crystals;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.MaterialCrystalStructure;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialCrystalBase;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialCrystalDiamond extends MaterialCrystalBase
{
	@Override
	public String getName()
	{
		return "diamond";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
				new ElementProportion(PeriodicTableElement.CARBON)
		));
	}

	@Nonnull
	@Override
	public Rarity getRarity()
	{
		return Rarity.RARE;
	}

	@Override
	public int getBoilingPoint()
	{
		return 2503;
	}

	@Override
	public int getMeltingPoint()
	{
		return 1923;
	}

	public static int baseColor = 0x8FE0C8;

	@Override
	public int getColor(int temperature)
	{
		return baseColor;
	}

	@Override
	public float getHardness()
	{
		return 3;
	}

	@Override
	public float getMiningResistance()
	{
		return 2;
	}

	@Override
	public float getBlastResistance()
	{
		return 2;
	}

	@Override
	public float getDensity()
	{
		return 0.45f;
	}

	@Override
	public int getBlockHarvestLevel()
	{
		return 1;
	}

	@Override
	public MaterialCrystalStructure getCrystalStructure() {
		return MaterialCrystalStructure.ISOMETRIC;
	}

	@Override
	public MaterialCrystalStructure.LatticeStructure getLatticeStructure() {
		return MaterialCrystalStructure.LatticeStructure.octahedrons;
	}

	@Override
	public boolean hasRawCrystal()
	{
		return true;
	}
}
