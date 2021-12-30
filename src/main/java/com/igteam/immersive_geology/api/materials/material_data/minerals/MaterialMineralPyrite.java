package com.igteam.immersive_geology.api.materials.material_data.minerals;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGRoastingProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralPyrite  extends MaterialMineralBase
{
    @Override
    public String getName()
    {
        return "pyrite";
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
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR, 2)
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

    public static int baseColor = 0xD6C380;

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

    public IGMaterialProcess getProcessingMethod() {
        //A bit lazy approach
        IGRoastingProcessingMethod roasting_method = new IGRoastingProcessingMethod(100, 2);
        roasting_method.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(this,
                MaterialUseType.CRUSHED_ORE), 1));
        roasting_method.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Hematite.getMaterial(),
                MaterialUseType.CRUSHED_ORE), 1));

        inheritedProcessingMethods.add(roasting_method);
        return super.getProcessingMethod();
    }

}
