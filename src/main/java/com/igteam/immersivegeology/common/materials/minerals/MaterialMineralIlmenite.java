package com.igteam.immersivegeology.common.materials.minerals;

import java.util.Arrays;
import java.util.LinkedHashSet;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMineralBase;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import net.minecraft.item.Rarity;

public class MaterialMineralIlmenite extends MaterialMineralBase {

	@Override
	public String getName()
	{
		return "ilmenite";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}

	
	@Override 
	public EnumMineralType getMineralType() {
		// TODO Auto-generated method stub
		return EnumMineralType.MINERAL;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements() {
		// TODO Auto-generated method stub
		return new LinkedHashSet<>(Arrays.asList(
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.TITANIUM),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
		));
	}

	@Override
	public Rarity getRarity() {
		// TODO Auto-generated method stub
		return Rarity.RARE;
	}

	@Override
	public int getBoilingPoint() {
		// TODO Auto-generated method stub
		return 3923;
	}

	@Override
	public int getMeltingPoint() {
		// TODO Auto-generated method stub
		return 1923;
	}

	public static int baseColor = 0x4A3E3E;
	
	@Override
	public int getColor(int temperature) {
		// TODO Auto-generated method stub
		return baseColor;
	}

	@Override
	public float getHardness() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public float getMiningResistance() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float getBlastResistance() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public float getDensity() {
		// TODO Auto-generated method stub
		return 4.55f;
	}
}
