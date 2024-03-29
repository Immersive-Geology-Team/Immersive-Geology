package generators.tags;

import blusunrize.immersiveengineering.api.Lib;
import igteam.immersive_geology.ImmersiveGeology;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.GasEnum;
import igteam.api.materials.SlurryEnum;
import igteam.api.materials.data.fluid.MaterialBaseFluid;
import igteam.api.materials.data.gas.MaterialBaseGas;
import igteam.api.materials.data.slurry.variants.MaterialSlurryWrapper;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.FluidPattern;
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
        for (FluidPattern pattern : FluidPattern.values()) {
            for (MaterialInterface<MaterialBaseFluid> fluid : FluidEnum.values()) {
                if (fluid.hasPattern(pattern)) {
                    Fluid fluidBlock = fluid.getFluid(pattern);
                    if (fluidBlock != null) {
                        getOrCreateBuilder(fluid.getFluidTag(pattern)).add(fluidBlock);
                    }
                }
            }

            for (MaterialInterface<MaterialBaseGas> gas : GasEnum.values()) {
                if (gas.hasPattern(pattern)) {
                    Fluid fluidBlock = gas.getFluid(pattern);
                    if (fluidBlock != null) {
                        getOrCreateBuilder(gas.getFluidTag(pattern)).add(fluidBlock);
                    }
                }
            }
        }

        log.info("Slurry Fluid Tag Registration");
        for(SlurryEnum wrapper : SlurryEnum.values()){
            for(MaterialSlurryWrapper slurry : wrapper.getEntries()) {
                if(slurry != null) {
                    Fluid fluidBlock = slurry.getFluid(FluidPattern.slurry);
                    if (fluidBlock != null) {
                        log.info("Slurry: " + slurry.getFluidTag(FluidPattern.slurry).toString());
                        getOrCreateBuilder(slurry.getFluidTag(FluidPattern.slurry)).add(fluidBlock);
                    }
                }
            }
        }
    }
}
