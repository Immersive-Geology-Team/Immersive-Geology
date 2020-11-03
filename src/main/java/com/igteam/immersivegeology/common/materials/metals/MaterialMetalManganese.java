package com.igteam.immersivegeology.common.materials.metals;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by Crimson on 31-03-2020.
 */
public class MaterialMetalManganese extends MaterialMetalBase
{
	@Override
	public String getName()
	{
		return "manganese";
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
				new ElementProportion(PeriodicTableElement.MANGANESE)
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
		return 2334;
	}

	@Override
	public int getMeltingPoint()
	{
		return 1519;
	}

	@Override
	public int getColor(int temperature)
	{
		return 0xc4aead;
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
		return 12.0F;
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
		return 1.2f;
	}

	//Stone pickaxe level
	@Override
	public int getBlockHarvestLevel()
	{
		return 2;
	}

	@Override
	public EnumMetalType getMetalType()
	{
		return EnumMetalType.METAL;
	}

	@Override
	public int getHeadDurability() {
		return 20;
	}

	@Override
	public int getHeadMiningLevel() {
		return 1;
	}

	@Override
	public int getHeadEnchantability() {
		return 7;
	}

	@Override
	public int getHeadMiningSpeed() {
		return 4;
	}

	@Override
	public int getHeadAttackSpeed() {
		return 1;
	}

	@Override
	public int getHeadAttackDamage() {
		return 1;
	}

	/*@Nullable
	@Override
	public IItemTier getToolTier()
	{
		return IGContent.;
	}*/
}
