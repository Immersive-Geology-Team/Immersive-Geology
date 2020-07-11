package com.igteam.immersivegeology.common.materials.metals.alloys;

import java.util.Arrays;
import java.util.LinkedHashSet;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import net.minecraft.item.Rarity;

public class MaterialMetalSteel extends MaterialMetalBase {
	
	@Override
	public String getName()
	{
		return "steel";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return ImmersiveEngineering.MODID;
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
		return 3200;
	}

	@Override
	public int getMeltingPoint() {
		// TODO Auto-generated method stub
		return 1698;
	}

	@Override
	public int getColor(int temperature) {
		// TODO Auto-generated method stub
		return 0x43464B;
	}

	@Override
	public float getHardness() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public float getMiningResistance() {
		// TODO Auto-generated method stub
		return 8;
	}

	@Override
	public float getBlastResistance() {
		// TODO Auto-generated method stub
		return 12;
	} 

	@Override
	public float getDensity() {
		// TODO Auto-generated method stub
		return 7.800f; // gm/cm^3
	}

}
