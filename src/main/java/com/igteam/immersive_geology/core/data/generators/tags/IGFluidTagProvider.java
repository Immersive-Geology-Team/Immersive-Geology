package com.igteam.immersive_geology.core.data.generators.tags;

import blusunrize.immersiveengineering.api.Lib;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.core.data.generators.helpers.IGTags;
import com.igteam.immersive_geology.core.registration.IGVariantHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;

public class IGFluidTagProvider extends FluidTagsProvider
{
    Logger log = ImmersiveGeology.getNewLogger();

    public IGFluidTagProvider(DataGenerator gen, ExistingFileHelper existingFileHelper)
    {
        super(gen, Lib.MODID, existingFileHelper);
    }

    @Override
    protected void registerTags()
    {
        log.info("Fluid Tag Registration");
        for(MaterialEnum material : MaterialEnum.fluidValues()) {
            IGTags.MaterialTags tags = IGTags.getTagsFor(material);

            Fluid fluid = MaterialUseType.FLUIDS.getFluid(material, false);
            getOrCreateBuilder(tags.fluid).add(fluid);

            log.debug("Fluid: " + tags.fluid.getName());
        }
    }
}
