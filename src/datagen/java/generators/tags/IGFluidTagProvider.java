package generators.tags;

import blusunrize.immersiveengineering.api.Lib;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.legacy_api.materials.Material;
import com.igteam.immersive_geology.legacy_api.materials.MaterialEnum;
import com.igteam.immersive_geology.legacy_api.materials.MaterialUseType;
import com.igteam.immersive_geology.legacy_api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.legacy_api.materials.fluid.SlurryEnum;
import com.igteam.immersive_geology.legacy_api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.legacy_api.materials.material_bases.MaterialMineralBase;
import com.igteam.immersive_geology.legacy_api.materials.material_data.fluids.slurry.MaterialSlurryWrapper;
import com.igteam.immersive_geology.legacy_api.tags.IGTags;
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
        for(FluidEnum wrapper : FluidEnum.values()) {
            MaterialFluidBase material = wrapper.getMaterial();
            IGTags.MaterialTags tags = IGTags.getTagsFor(material);
            Fluid fluid = MaterialUseType.FLUIDS.getFluid(material, false);
            getOrCreateBuilder(tags.fluid).add(fluid);
            log.info("Fluid: " + tags.fluid.getName());
        }

        log.info("Slurry Fluid Tag Registration");
        for(SlurryEnum wrapper : SlurryEnum.values()){
            for(MaterialSlurryWrapper slurry : wrapper.getEntries()) {
                IGTags.MaterialTags tags = IGTags.getTagsFor(slurry);
                Fluid fluid = MaterialUseType.SLURRY.getSlurry(slurry.getSoluteMaterial(), slurry.getBaseFluidMaterial(), false);
                getOrCreateBuilder(tags.fluid).add(fluid);
                log.info("Slurry: " + tags.fluid.getName());
            }
        }
    }
}
