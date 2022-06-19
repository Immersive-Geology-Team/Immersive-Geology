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

public class MaterialMineralZircon extends MaterialBaseMineral {

    public  MaterialMineralZircon() {
        super("zircon");
        initializeColorMap((p) -> 0x8B2E1D);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
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
        // TODO Auto-generated method stub
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ZIRCONIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
        ));
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.UNCOMMON;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        return Collections.emptySet();
    }
}
