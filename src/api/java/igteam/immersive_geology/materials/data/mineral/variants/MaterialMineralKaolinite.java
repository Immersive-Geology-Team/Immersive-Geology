package igteam.immersive_geology.materials.data.mineral.variants;

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

public class MaterialMineralKaolinite extends MaterialBaseMineral {

    public MaterialMineralKaolinite() {
        super("kaolinite");
        initializeColorMap((p) -> 0xE5DFD1);
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
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        // TODO Auto-generated method stub
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 5)
        ));
    }

    @Override
    public Rarity getRarity() {
        // TODO Auto-generated method stub
        return Rarity.COMMON;
    }

    @Override
    protected boolean hasCrystal() {
        return false;
    }

    @Override
    protected boolean hasClay() {
        return true;
    }

    @Override
    protected boolean hasStorageBlock() {
        return true;
    }

    @Override
    protected boolean hasOreBlock() {
        return false;
    }

    @Override
    protected boolean hasOreBit() {
        return false;
    }

    @Override
    protected boolean hasOreChunk() {
        return false;
    }

    @Override
    protected boolean hasCrushedOre() {
        return false;
    }

    @Override
    protected boolean hasDirtyCrushedOre() {
        return false;
    }
    @Override
    protected boolean hasDust() {
        return false;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        return Collections.emptySet();
    }
}
