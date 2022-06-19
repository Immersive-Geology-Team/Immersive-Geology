package igteam.immersive_geology.materials.data.mineral.variants;

import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.processing.IGProcessingStage;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralLimestone extends MaterialBaseMineral {

    public MaterialMineralLimestone() {
        super("limestone");
        initializeColorMap((p) -> 0xffffff);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return null;
    }

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
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CALCIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.CARBON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
        )
        );
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.COMMON;
    }
    @Override
    protected boolean hasCrystal() {
        return false;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        return Collections.emptySet();
    }
}
