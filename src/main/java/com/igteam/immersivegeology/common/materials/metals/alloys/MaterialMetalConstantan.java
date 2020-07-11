package com.igteam.immersivegeology.common.materials.metals.alloys;

import java.util.Arrays;
import java.util.LinkedHashSet;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import net.minecraft.item.Rarity;

public class MaterialMetalConstantan extends MaterialMetalBase {

	@Override
	public String getName()
	{
		return "constantan";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}
	
	@Override
	public EnumMetalType getMetalType() {
		// TODO Auto-generated method stub
		return EnumMetalType.ALLOY;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements() {
		// TODO Auto-generated method stub
		return new LinkedHashSet<>(Arrays.asList(
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.COPPER),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.NICKEL)));
	}

	@Override
	public Rarity getRarity() {
		// TODO Auto-generated method stub
		return Rarity.UNCOMMON;
	}

	@Override
	public int getBoilingPoint() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMeltingPoint() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColor(int temperature) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getHardness() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getMiningResistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getBlastResistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getDensity() {
		// TODO Auto-generated method stub
		return 8.885f; // g/cm^3
	}

}
