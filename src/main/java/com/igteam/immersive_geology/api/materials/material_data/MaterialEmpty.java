package com.igteam.immersive_geology.api.materials.material_data;

import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.MaterialTypes;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;

/**
 * Created by Pabilo8 on 25-03-2020.
 * A material (or actually lack of one) for compatibility, less crashing if a material has been removed
 */
public class MaterialEmpty extends Material
{
	@Override
	public String getName()
	{
		return "missingno";
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
		return new LinkedHashSet<>();
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.EPIC;
	}

	@Nonnull
	@Override
	public MaterialTypes getMaterialType()
	{
		return MaterialTypes.STONE;
	}
	
	@Override
	public MaterialTypes getMaterialSubType()
	{
		return MaterialTypes.STONE;
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

	@Override
	public int getColor(int temperature)
	{
		return 0xffffff;
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

	/*@Nullable
	@Override
	public IItemTier getToolTier()
	{
		return IGContent.;
	}*/

	@Override
	public boolean preExists() {
		return false;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return null;
	}

	@Override
	public MaterialEnum getSecondaryType() {
		return null;
	}

	@Override
	public boolean hasSubtype(MaterialUseType useType) {
		return false;
	}
}
