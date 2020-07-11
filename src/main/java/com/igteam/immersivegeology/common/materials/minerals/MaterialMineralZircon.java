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

public class MaterialMineralZircon extends MaterialMineralBase {

	@Override
	public String getName()
	{
		return "zircon";
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
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.ZIRCONIUM),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
		));
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
	public static int baseColor = 0x8B2E1D;
	
	@Override
	public int getColor(int temperature) {
		// TODO Auto-generated method stub
		return baseColor;
	}

	@Override
	public float getHardness() {
		// TODO Auto-generated method stub
		return 7.5f;
	}

	@Override
	public float getMiningResistance() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public float getBlastResistance() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public float getDensity() {
		// TODO Auto-generated method stub
		return 0;
	}

}
