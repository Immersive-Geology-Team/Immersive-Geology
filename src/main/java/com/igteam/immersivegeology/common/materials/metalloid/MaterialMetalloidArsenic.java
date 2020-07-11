package com.igteam.immersivegeology.common.materials.metalloid;

import java.util.LinkedHashSet;

import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalloidBase;

import net.minecraft.item.Rarity;

public class MaterialMetalloidArsenic extends MaterialMetalloidBase {

	@Override
	public EnumMetalType getMetalType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rarity getRarity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBoilingPoint() {
		// TODO Auto-generated method stub
		return 887;
	}

	@Override
	public int getMeltingPoint() {
		// TODO Auto-generated method stub
		return 817;
	}

	@Override
	public int getColor(int temperature) {
		// TODO Auto-generated method stub
		return 0x3B444B;
	}

	@Override
	public float getHardness() {
		// TODO Auto-generated method stub
		return 1;
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
		return 5.73f;
	}

}
