package igteam.immersive_geology.materials.data.metal.variants;

import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGStageDesignation;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalNickel extends MaterialBaseMetal {

    public MaterialMetalNickel() {
        super("nickel");
        initializeColorMap((p) -> 0x7FFFD4);
    }

    @Override
    public boolean isNative() {
        return true;
    }


    @Override
    public boolean hasMetalOxide () {return true;}
    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
    public boolean hasCrystal() {return true;}

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.NICKEL)
        ));
    }
}