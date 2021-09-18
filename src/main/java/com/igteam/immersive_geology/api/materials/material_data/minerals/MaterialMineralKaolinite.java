package com.igteam.immersive_geology.api.materials.material_data.minerals;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.MaterialTypes;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;

import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Rarity;

public class MaterialMineralKaolinite extends MaterialMineralBase {
	
	@Override
	public String getName()
	{
		return "kaolinite";
	}

	@Override
	public String getModID()
	{
		return IGLib.MODID;
	}

	@Override
	public EnumMineralType getMineralType() {
		// TODO Auto-generated method stub
		return EnumMineralType.CLAY;
	}

	@Override
	public MaterialTypes getMaterialSubType() {
		// TODO Auto-generated method stub
		return MaterialTypes.STONE; //do not want in world ore bearing rocks!
	}
	
	@Override
	public LinkedHashSet<ElementProportion> getElements() {
		// TODO Auto-generated method stub
		return new LinkedHashSet<>(Arrays.asList(
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM, 2),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON, 2),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 5)
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
		return 9999;
	}

	@Override
	public int getMeltingPoint() {
		// TODO Auto-generated method stub
		return 930;
	}
	
	public static int baseColor = 0xE5DFD1;
	
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
		return 1;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return MaterialEnum.Aluminium;
	}
}
