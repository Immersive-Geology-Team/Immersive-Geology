/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.enums;

import com.igteam.immersivegeology.core.material.data.chemical.*;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public enum ChemicalEnum implements MaterialInterface<MaterialChemical>
{
    SulfuricAcid(new MaterialSulfuricAcid()),
    SulfurDioxde(new MaterialSulfurDioxide()),
    Brine(new MaterialBrine()),
    BindingAgent(new MaterialBindingAgent()),
    HydrochloricAcid(new MaterialHydrochloricAcid()),
    HydrofluoricAcid(new MaterialHydrofluoricAcid()),
    NitricAcid(new MaterialNitricAcid()),
    SodiumHydroxide(new MaterialSodiumHydroxide()),
    AquaRegia(new MaterialAquaRegia()),
    Ammonia(new MaterialAmmoniaSolution()),
    ChemicalWaste(new MaterialChemicalWaste());

    private final MaterialChemical material;
    ChemicalEnum(MaterialChemical m){
        this.material = m;
    }
    @Override
    public MaterialChemical instance() {
        return material;
    }

    public Fluid getSlurryWith(MaterialInterface<?> metalEnum)
    {
        return material.getFluid(BlockCategoryFlags.SLURRY, metalEnum);
    }

    public Fluid getSlurryWith(MaterialHelper helper)
    {
        return material.getFluid(BlockCategoryFlags.SLURRY, helper);
    }

    public FluidStack getSlurryWith(MaterialInterface<?> metalEnum, int amount)
    {
        return new FluidStack(getSlurryWith(metalEnum), amount);
    }

    public TagKey<Fluid> getCloudySlurryTagWith(MaterialInterface<?> metalEnum)
    {
        return getFluidTag(BlockCategoryFlags.CLOUDY_SLURRY, metalEnum);
    }

    public TagKey<Fluid> getCloudySlurryTagWith(MaterialHelper helper)
    {
        return getFluidTag(BlockCategoryFlags.CLOUDY_SLURRY, helper);
    }

    public Fluid getCloudySlurryWith(MaterialInterface<?> metalEnum)
    {
        return material.getFluid(BlockCategoryFlags.CLOUDY_SLURRY, metalEnum);
    }

    public Fluid getCloudySlurryWith(MaterialHelper helper)
    {
        return material.getFluid(BlockCategoryFlags.CLOUDY_SLURRY, helper);
    }

    public FluidStack getCloudySlurryWith(MaterialInterface<?> metalEnum, int amount)
    {
        return new FluidStack(getCloudySlurryWith(metalEnum), amount);
    }

    public TagKey<Fluid> getSlurryTagWith(BlockCategoryFlags type, MaterialInterface<?> material)
    {
        return getFluidTag(type, material);
    }

    public TagKey<Fluid> getSlurryTagWith(BlockCategoryFlags type, MaterialHelper helper)
    {
        return getFluidTag(type, helper);
    }


    public TagKey<Fluid> getSlurryTagWith(MaterialInterface<?> material)
    {
        return getFluidTag(BlockCategoryFlags.SLURRY, material);
    }

    public boolean hasSlurryWith(MaterialInterface<?> material)
    {
        return instance().hasSlurryWith(material);
    }

    public FluidStack getFluidStack(int i)
    {
        return new FluidStack(getFluid(BlockCategoryFlags.FLUID), i);
    }

    public boolean hasComplexNamingScheme()
    {
        return instance().hasComplexNamingScheme();
    }
}
