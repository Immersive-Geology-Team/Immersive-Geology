package igteam.api.materials.data.metal.variants;

import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.processing.IGProcessingStage;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalChromium extends MaterialBaseMetal {

    public MaterialMetalChromium() {
        super("chromium");
        initializeColorMap((p) -> 0xD7B4F3);
    }

    @Override
    protected boolean hasDust() {
        return true;
    }

    @Override
    protected boolean hasCrystal() {
        return true;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CHROMIUM)
        ));
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, "Crystal Processing"){
            @Override
            protected void describe() {

            }
        };
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }
}
