package com.igteam.immersive_geology.api.materials.material_data.stones;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialStoneBase;
import com.igteam.immersive_geology.core.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by JStocke12 on 27-03-2020.
 */
public class MaterialStoneNetherrack extends MaterialStoneBase
{
	//Rhyolite is the default minecraft stone, renamed
	@Override
	public String getName()
	{
		return "netherrack";
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
				new ElementProportion(PeriodicTableElement.ALUMINIUM)
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
		return 2000+273+400;
	}

	@Override
	public int getMeltingPoint()
	{
		return 2000+273;
	}

	@Override
	public int getColor(int temperature)
	{
		return 0x4e1e25;
	}

	//Needs to be changed in code for subtypes, such as sheetmetal
	@Override
	public float getHardness()
	{
		return 0.4F;
	}

	@Override
	public float getMiningResistance()
	{
		return 0.4F;
	}

	@Override
	public float getBlastResistance()
	{
		return 0.4F;
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
		return 1;
	}

	@Override
	public EnumStoneType getStoneType()
	{
		return EnumStoneType.IGNEOUS_EXTRUSIVE;
	}


	/*@Nullable
	@Override
	public IItemTier getToolTier()
	{
		return IGContent.;
	}*/
}
