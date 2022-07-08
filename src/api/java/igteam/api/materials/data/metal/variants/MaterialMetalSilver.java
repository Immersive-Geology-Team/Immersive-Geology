package igteam.api.materials.data.metal.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalSilver extends MaterialBaseMetal {

    public MaterialMetalSilver() {
        super("silver");
        initializeColorMap((p) -> 0xe7e7f7);
    }

    @Override
    public boolean isNative() {
        return true;
    }

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
    public boolean hasCompoundDust() {return true;}
    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILVER)
        ));
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, "Processing Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.bloomery(this).create(
                        "crushed_ore_" + getName() + "_to_ingot",
                        getParentMaterial().getStack(ItemPattern.crushed_ore, 2),
                        MetalEnum.Silver.getStack(ItemPattern.ingot),
                        120);
            }
        };
    }
}
