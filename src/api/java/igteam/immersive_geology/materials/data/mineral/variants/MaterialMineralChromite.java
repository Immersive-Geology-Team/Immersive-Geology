package igteam.immersive_geology.materials.data.mineral.variants;

import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.helper.PeriodicTableElement.ElementProportion;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralChromite extends MaterialBaseMineral {

    public MaterialMineralChromite() {
        super("chromite");
    }

    @Override
    public int getColor(MaterialPattern p) {
        return 0x615964;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    } // Specifically Hexoctahedral

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this,"Extraction Stage") {
            @Override
            protected void describe() {

            }
        };
    }

    @Override
    public LinkedHashSet<ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CHROMIUM, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
        )
        );
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.COMMON;
    }
}
