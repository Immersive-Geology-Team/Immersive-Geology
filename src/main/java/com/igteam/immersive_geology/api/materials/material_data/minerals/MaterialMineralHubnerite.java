package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.IGMineralProcess;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement.ElementProportion;
import com.igteam.immersive_geology.api.materials.helper.ProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import javafx.util.Pair;
import net.minecraft.item.Rarity;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralHubnerite extends MaterialMineralBase
{
	@Override
	public String getName()
	{
		return "hubnerite";
	}

	@Override
	public String getModID()
	{
		return IGLib.MODID;
	}

	@Override
	public LinkedHashSet<ElementProportion> getElements()
	{
		return new LinkedHashSet<>(Arrays.asList(
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.MANGANESE),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.TUNGSTEN),
				new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
		));
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.RARE;
	}

	@Override
	public int getBoilingPoint()
	{
		return 6202;
	}

	@Override
	public int getMeltingPoint()
	{
		return 3695;
	}

	@Override
	public EnumMineralType getMineralType()
	{
		return EnumMineralType.CRYSTAL;
	}


	public static int baseColor = 0x32332E;

	@Override
	public int getColor(int temperature)
	{
		return baseColor;
	}

	@Override
	public float getHardness()
	{
		return 0;
	}

	@Override
	public float getMiningResistance()
	{
		return 0;
	}

	@Override
	public float getBlastResistance()
	{
		return 0;
	}

	@Override
	public float getDensity()
	{
		return 0;
	}

	@Override
	public int getBlockHarvestLevel()
	{
		return 0;
	}

	@Override
	public CrystalFamily getCrystalFamily() {
		return CrystalFamily.MONOCLINIC;
	}

	@Override
	public MaterialEnum getProcessedType() {
		return MaterialEnum.Tungsten;
	}

	@Override
	public MaterialEnum getSecondaryType() {
		return MaterialEnum.Manganese;
	}

	//Input the processing steps for this material
	@Override
	public Pair<ProcessingMethod, IGMineralProcess> getProcessingMethod() {
		return new Pair<>(ProcessingMethod.ACID,
				new IGMineralProcess(new Pair<MaterialEnum, MaterialUseType>(MaterialEnum.Hubnerite, MaterialUseType.SLURRY),
						new FluidStack(IGRegistrationHolder.getFluidByMaterial(MaterialEnum.HydrochloricAcid.getMaterial(), false), 125),
						this, MaterialUseType.CRUSHED_ORE));
	}

	@Override
	public boolean hasSlurry() {
		return true;
	}

	public MaterialFluidBase getSlurryBaseFluid(){
		return (MaterialFluidBase) MaterialEnum.HydrochloricAcid.getMaterial();
	}
}