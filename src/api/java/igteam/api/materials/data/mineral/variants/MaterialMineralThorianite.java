package igteam.api.materials.data.mineral.variants;

import igteam.api.materials.MetalEnum;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGStageDesignation;
import net.minecraft.item.Rarity;
import net.minecraft.world.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralThorianite extends MaterialBaseMineral {

    public MaterialMineralThorianite() {
        super("thorianite");
        initializeColorMap((p) -> 0x485560);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.extraction) {
            @Override
            protected void describe() {

            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.THORIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2)
        ));
    }


    @Override
    public Rarity getRarity()
    {
        return Rarity.RARE;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Thorium);

        return sources;
    }

}
