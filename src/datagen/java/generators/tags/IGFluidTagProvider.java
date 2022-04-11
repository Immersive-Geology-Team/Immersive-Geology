package generators.tags;

import blusunrize.immersiveengineering.api.Lib;
import com.igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.SlurryEnum;
import igteam.immersive_geology.materials.data.fluid.MaterialBaseFluid;
import igteam.immersive_geology.materials.data.slurry.variants.MaterialSlurryWrapper;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.MiscPattern;
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

        for (MaterialInterface<MaterialBaseFluid> fluid : FluidEnum.values()) {
            for (MiscPattern pattern : MiscPattern.values()) {
                if (fluid.hasPattern(pattern)) {
                    Fluid fluidBlock = fluid.getFluid(pattern);
                    if (fluidBlock != null) {
                        getOrCreateBuilder(fluid.getFluidTag(pattern)).add(fluidBlock);
                    }
                }
            }
        }

        log.info("Slurry Fluid Tag Registration");
        for(SlurryEnum wrapper : SlurryEnum.values()){
            for(MaterialSlurryWrapper slurry : wrapper.getEntries()) {
                if(slurry != null) {
                    Fluid fluidBlock = slurry.getFluid(MiscPattern.slurry);
                    if (fluidBlock != null) {
                        log.info("Slurry: " + slurry.getFluidTag(MiscPattern.slurry).toString());
                        getOrCreateBuilder(slurry.getFluidTag(MiscPattern.slurry)).add(fluidBlock);
                    }
                }
            }
        }
    }
}
