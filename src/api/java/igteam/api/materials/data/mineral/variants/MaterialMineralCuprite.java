package igteam.api.materials.data.mineral.variants;

import igteam.api.materials.MetalEnum;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralCuprite extends MaterialBaseMineral {

    public MaterialMineralCuprite() {
        super("cuprite");
        initializeColorMap((p) -> 0x830922);
    }

    @Override
    public boolean hasDust() {
        return false;
    }

    @Override
    public boolean hasCrystal() { return false;}

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
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
                        MetalEnum.Copper.getStack(ItemPattern.ingot));
                IRecipeBuilder.bloomery(this).create(
                        "crushed_ore_" + getName() + "_to_ingot",
                        getParentMaterial().getStack(ItemPattern.crushed_ore, 2),
                        MetalEnum.Copper.getStack(ItemPattern.ingot),
                        120);
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(
                Arrays.asList(
                        new PeriodicTableElement.ElementProportion(PeriodicTableElement.COPPER),
                        new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2)
                )
        );
    }

    @Override
    public Rarity getRarity()
    {
        // TODO Auto-generated method stub
        return Rarity.COMMON;
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Copper);

        return sources;
    }
}
