package generators.tags;

import blusunrize.immersiveengineering.api.Lib;
import com.igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.materials.FluidEnum;
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

        for (MaterialInterface fluid : FluidEnum.values()) {
            for (MiscPattern pattern : MiscPattern.values()) {
                if (fluid.hasPattern(pattern)) {
                    Fluid fluidBlock = fluid.getFluid(pattern);
                    getOrCreateBuilder(fluid.getFluidTag(pattern)).add(fluidBlock);
                }
            }
        }

//        log.info("Slurry Fluid Tag Registration");
//        for(SlurryEnum wrapper : SlurryEnum.values()){
//            for(MaterialSlurryWrapper slurry : wrapper.getEntries()) {
//                IGTags.MaterialTags tags = IGTags.getTagsFor(slurry);
//                Fluid fluid = slurry.getSoluteMaterial(), slurry.getFluidBase(), false);
//                getOrCreateBuilder(tags.fluid).add(fluid);
//                log.info("Slurry: " + tags.fluid.getName());
//            }
//        }
    }
}
