package com.igteam.immersivegeology.common.materials.minerals;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement;
import com.igteam.immersivegeology.api.materials.PeriodicTableElement.ElementProportion;
import com.igteam.immersivegeology.api.materials.material_bases.MaterialMineralBase;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralKaolinite extends MaterialMineralBase {
	
	@Override
	public String getName()
	{
		return "kaolinite";
	}

	@Override
	public String getModID()
	{
		return ImmersiveGeology.MODID;
	}

	@Override
	public EnumMineralType getMineralType() {
		return EnumMineralType.MINERAL;
	}
	
	@Override
	public LinkedHashSet<ElementProportion> getElements() {
		return new LinkedHashSet<>(Arrays.asList(
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM, 2),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON, 2),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 5)
		));
	}

	@Override
	public boolean hasUsetype(MaterialUseType useType)
	{
		if(useType==MaterialUseType.GENERATED_ORE)
			return false; //Has no ore
		if(useType==MaterialUseType.ROCK)
			return true;
		return super.hasUsetype(useType);
	}

	@Override
	public Rarity getRarity() {
		return Rarity.COMMON;
	}

	@Override
	public int getBoilingPoint() {
		return 9999;
	}

	@Override
	public int getMeltingPoint() {
		return 930;
	}
	
	public static int baseColor = 0xE5DFD1;
	
	@Override
	public int getColor(int temperature) {
		return baseColor;
	}

	@Override
	public float getHardness() {
		return 2;
	}

	@Override
	public float getMiningResistance() {
		return 1;
	}

	@Override
	public float getBlastResistance() {
		return 1;
	}

	@Override
	public float getDensity() {
		return 1;
	}

}
