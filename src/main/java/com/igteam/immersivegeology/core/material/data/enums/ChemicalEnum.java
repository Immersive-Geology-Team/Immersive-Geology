/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.enums;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import com.igteam.immersivegeology.common.fluid.IGFluid;
import com.igteam.immersivegeology.core.material.data.chemical.*;
import com.igteam.immersivegeology.core.material.data.chemical.mantle.*;
import com.igteam.immersivegeology.core.material.data.stone.compat.adastra.*;
import com.igteam.immersivegeology.core.material.data.stone.compat.tfc.*;
import com.igteam.immersivegeology.core.material.data.stone.vanilla.*;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.data.types.MaterialStone;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public enum ChemicalEnum implements MaterialInterface<MaterialChemical>
{
    SulfuricAcid(new MaterialSulfuricAcid()),
    SulfurDioxde(new MaterialSulfurDioxide()),
    Brine(new MaterialBrine()),
    HydrochloricAcid(new MaterialHydrochloricAcid()),
    HydrofluoricAcid(new MaterialHydrofluoricAcid()),
    NitricAcid(new MaterialNitricAcid()),
    SodiumHydroxide(new MaterialSodiumHydroxide()),

    // Mantle Fluid
    MoltenMantle(new MaterialMoltenMantle());

    private final MaterialChemical material;
    ChemicalEnum(MaterialChemical m){
        this.material = m;
    }
    @Override
    public MaterialChemical instance() {
        return material;
    }

    public Fluid getSlurryWith(MetalEnum metalEnum)
    {
        return material.getFluid(BlockCategoryFlags.SLURRY, metalEnum);
    }

    public FluidStack getSlurryWith(MetalEnum metalEnum, int amount)
    {
        return new FluidStack(getSlurryWith(metalEnum), amount);
    }

    public TagKey<Fluid> getSlurryTagWith(MetalEnum metalEnum)
    {
        return getFluidTag(BlockCategoryFlags.SLURRY, metalEnum);
    }

    public boolean hasSlurryMetal(MetalEnum metal)
    {
        return instance().hasSlurryMetal(metal);
    }
}
