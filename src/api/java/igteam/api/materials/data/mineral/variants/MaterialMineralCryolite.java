package igteam.api.materials.data.mineral.variants;

import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.processing.IGProcessingStage;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralCryolite extends MaterialBaseMineral {

    public MaterialMineralCryolite() {
        super("cryolite");
        initializeColorMap((p) -> 0xC5C5C5);
    }


    @Override
    public boolean isSalt() { return true; }

    @Override
    protected boolean hasCrystal() { return true; }

    @Override
    protected boolean hasOreBit() { return false; }

    @Override
    protected boolean hasOreChunk() { return false; }

    @Override
    protected boolean hasDirtyCrushedOre() { return false; }

    @Override
    protected boolean hasCrushedOre() { return false; }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.refinement) {
            @Override
            protected void describe() {

            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.FLUORINE, 6),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SODIUM, 3)
        )
        );
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.EPIC;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        return Collections.emptySet();
    }

    @Override
    public MaterialSourceWorld getDimension() {
        return MaterialSourceWorld.end;
    }
}
