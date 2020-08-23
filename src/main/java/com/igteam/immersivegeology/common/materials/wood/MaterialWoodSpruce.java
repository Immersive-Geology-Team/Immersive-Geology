package com.igteam.immersivegeology.common.materials.wood;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialWoodBase;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * @author Pabilo8
 * @since 22.08.2020
 */
public class MaterialWoodSpruce extends MaterialWoodBase
{
	//Rhyolite is the default minecraft stone, renamed
	@Override
	public String getName()
	{
		return "spruce";
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
				new ElementProportion(PeriodicTableElement.CARBON),
				new ElementProportion(PeriodicTableElement.HYDROGEN),
				new ElementProportion(PeriodicTableElement.OXYGEN)
		));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.COMMON;
	}

	//Wood has no boiling point, though cellulose starts to decompose around 250 Celsius, and after 1000 Celsius it's gone ^^
	@Override
	public int getBoilingPoint()
	{
		return 1000+273;
	}

	//An average value
	// TODO: 22.08.2020 input real values
	@Override
	public int getMeltingPoint()
	{
		return 250+273;
	}

	@Override
	public int getColor(int temperature)
	{
		return 0x614b2e;
	}

	//Needs to be changed in code for subtypes, such as sheetmetal
	@Override
	public float getHardness()
	{
		return 0.5F;
	}

	@Override
	public float getMiningResistance()
	{
		return 3.0F;
	}

	@Override
	public float getBlastResistance()
	{
		return 4f;
	}

	//Copied from Immersive Intelligence (steel has i think 1.65, leaves 0.35)
	@Override
	public float getDensity()
	{
		return 0.85f;
	}

	//Stone pickaxe level
	@Override
	public int getBlockHarvestLevel()
	{
		return 0;
	}

	/*@Nullable
	@Override
	public IItemTier getToolTier()
	{
		return IGContent.;
	}*/
}
