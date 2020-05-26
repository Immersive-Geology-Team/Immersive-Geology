package com.igteam.immersivegeology.common.materials.rocks;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialRockBase;

import net.minecraft.item.Rarity;

public class MaterialLimestone extends MaterialRockBase {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "limestone";
	}
	
	@Override
	public String getModID() {
		// TODO Auto-generated method stub
		return ImmersiveGeology.MODID;
	}
	
	@Override
	public EnumRockType getRockType() {
		// TODO Auto-generated method stub
		return EnumRockType.SEDIMENTARY;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements() {
		// TODO Auto-generated method stub
		return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CALCIUM, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CARBON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
        ));
	}

	@Override
	public Rarity getRarity() {
		// TODO Auto-generated method stub
		return Rarity.COMMON;
	}

	@Override
	public int getBoilingPoint() {
		return 1200;//this is a guess
	}

	@Override
	public int getMeltingPoint() {
		return 440;
	}

	@Override
	public int getColor(int temperature) {
		// TODO Auto-generated method stub
		return 0x996600;
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
		return 0;
	}

	@Override
	public int getBlockHarvestLevel() {
		// TODO Auto-generated method stub
		return 0;
	}


}
