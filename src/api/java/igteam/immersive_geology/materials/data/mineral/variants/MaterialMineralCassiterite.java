package igteam.immersive_geology.materials.data.mineral.variants;

import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.helper.PeriodicTableElement.ElementProportion;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralCassiterite extends MaterialBaseMineral {

    public MaterialMineralCassiterite() {
        super("cassiterite");
        initializeColorMap((p) -> 0x8f8b96);
    }
    @Override
    public boolean hasDust() {
        return false;
    }

    @Override
    public boolean hasCrystal() { return false;}
    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }

    @Override
    public LinkedHashSet<ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.TIN),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2)
        ));
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, "Processing Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.blasting(this).create(
                        "crushed_ore_" + getName() + "_to_ingot",
                        getParentMaterial().getItemTag(ItemPattern.crushed_ore),
                        MetalEnum.Tin.getStack(ItemPattern.ingot));
            }
        };
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Tin);

        return sources;
    }
}
