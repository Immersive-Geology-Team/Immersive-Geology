package igteam.immersive_geology.materials.data.gas.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.immersive_geology.materials.FluidEnum;
import igteam.immersive_geology.materials.GasEnum;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.gas.MaterialBaseGas;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGStageDesignation;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedHashSet;

public class MaterialSulphurDioxideGas extends MaterialBaseGas {
    public MaterialSulphurDioxideGas() {
        super("sulphur_dioxide");
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return null;
    }

    @Override
    public boolean isFluidPortable(ItemPattern bucket) {
        return false;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.synthesis) {
            @Override
            protected void describe() {
                //TODO -- add catalytic synthesis in 1.18.2 ~UnSchtalch
                IRecipeBuilder.synthesis(this).create(
                        "sulfuric_acid_from_oxide",
                        new FluidTagInput(GasEnum.SulphurDioxide.getFluidTag(FluidPattern.gas), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        new FluidStack(FluidEnum.SulfuricAcid.getFluid(FluidPattern.fluid), 125)
                );
            }
        };
    }
}
