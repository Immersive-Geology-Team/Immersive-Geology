package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.CrystalFamily;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGSeparationProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralWolframite extends MaterialMineralBase
{
    @Override
    public String getName()
    {
        return "wolframite";
    }

    @Override
    public String getModID()
    {
        return IGLib.MODID;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.MAGNESIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.TUNGSTEN),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
        ));
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.COMMON;
    }

    @Override
    public int getBoilingPoint()
    {
        return 2896;
    }

    @Override
    public int getMeltingPoint()
    {
        return 1870;
    }

    @Override
    public EnumMineralType getMineralType()
    {
        return EnumMineralType.CRYSTAL;
    }

    public static int baseColor = 0x3A3E49;

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

    @Override
    public IGMaterialProcess getProcessingMethod() {
        IGSeparationProcessingMethod wash_ore = new IGSeparationProcessingMethod(120);
        wash_ore.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this,
                MaterialUseType.CRUSHED_ORE)));

        wash_ore.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Ferberite.getMaterial(),
                MaterialUseType.CRUSHED_ORE)));
        //fix me later
        wash_ore.addItemWaste(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Hubnerite.getMaterial(),
                MaterialUseType.CRUSHED_ORE)));

        inheritedProcessingMethods.add(wash_ore);
        return super.getProcessingMethod();
    }
}
