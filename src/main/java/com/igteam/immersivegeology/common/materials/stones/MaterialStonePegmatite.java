package com.igteam.immersivegeology.common.materials.stones;

import java.util.Arrays;
import java.util.LinkedHashSet;

import javax.annotation.Nonnull;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialStoneBase;

import net.minecraft.item.Rarity;

public class MaterialStonePegmatite extends MaterialStoneBase {

	@Override
	public String getName()
	{
		return "pegamite";
	}

	@Nonnull
	@Override
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}
	
	@Override
	public EnumStoneType getStoneType() {
		// TODO Auto-generated method stub
		return EnumStoneType.IGNEOUS_INTRUSIVE;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
				new ElementProportion(PeriodicTableElement.ALUMINIUM)
		));
	}
	@Override
	public Rarity getRarity() {
		// TODO Auto-generated method stub
		return Rarity.COMMON;
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
		return 0xA78D5C;
	}

	@Override
	public float getHardness() {
		// TODO Auto-generated method stub
		return 1.5f;
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
		return 0;
	}

	@Override
	public int getBlockHarvestLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

}
