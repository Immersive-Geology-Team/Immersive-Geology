package igteam.api.materials.data.gas.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.materials.FluidEnum;
import igteam.api.materials.GasEnum;
import igteam.api.materials.data.gas.MaterialBaseGas;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.FluidFamily;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
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
    public boolean isFluidPortable(ItemFamily bucket) {
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
                        new FluidTagInput(GasEnum.SulphurDioxide.getFluidTag(FluidFamily.gas), 125),
                        new FluidTagInput(FluidTags.WATER, 125),
                        new FluidStack(FluidEnum.SulfuricAcid.getFluid(FluidFamily.fluid), 125)
                );
            }
        };
    }
}
