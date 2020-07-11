package com.igteam.immersivegeology.common.materials.metals.alloys;

import java.util.Arrays;
import java.util.LinkedHashSet;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMineralBase;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import net.minecraft.item.Rarity;

public class MaterialMetalElectrum extends MaterialMetalBase {
	
	@Override
	public String getName()
	{
		return "electrum";
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
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.GOLD),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILVER)));
	}

	@Override
	public Rarity getRarity() {
		// TODO Auto-generated method stub
		return Rarity.RARE;
	}

	@Override
	public int getBoilingPoint() {
		// TODO Auto-generated method stub
		return 3243;
	}

	@Override
	public int getMeltingPoint() {
		// TODO Auto-generated method stub
		return 1656; //could not find exact melting point, chose a value between gold and silver leaning more to silvers
	}

	@Override
	public int getColor(int temperature) {
		// TODO Auto-generated method stub
		return 0x94826A;
	}

	@Override
	public float getHardness() {
		// TODO Auto-generated method stub
		return 2.75f;
	}

	@Override
	public float getMiningResistance() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public float getBlastResistance() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public float getDensity() {
		// TODO Auto-generated method stub
		return 13.5f; //gm/cm^3
	}
}
