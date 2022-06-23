package igteam.immersive_geology.materials.data.mineral.variants;

import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class MaterialMineralChalcopyrite extends MaterialBaseMineral {

    public MaterialMineralChalcopyrite() {
        super("chalcopyrite");
        initializeColorMap((p) -> 0x5B4D2A);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }

    @Override
    protected boolean hasSlag() {
        return true;
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.COPPER),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR, 2)
        ));
    }


    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, "Processing Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.roast(this).create(
                        "mineral_" + getName() + "_to_slag",
                        getParentMaterial().getStack(ItemPattern.crushed_ore),
                        getParentMaterial().getStack(ItemPattern.slag), 1000, 1);
                IRecipeBuilder.crushing(this).create( "slag_" +getName() + "_to_dust",
                        getItemTag(ItemPattern.slag),
                        getStack(ItemPattern.dust), 3000, 200);
                IRecipeBuilder.separating(this).create(
                        getParentMaterial().getItemTag(ItemPattern.dust),
                        MetalEnum.Iron.getStack(ItemPattern.metal_oxide),
                        MetalEnum.Copper.getStack(ItemPattern.metal_oxide));
                IRecipeBuilder.blasting(this).create(
                        "oxide_" + MetalEnum.Copper.getName() + "_to_ingot",
                        MetalEnum.Copper.getItemTag(ItemPattern.metal_oxide),
                        MetalEnum.Copper.getStack(ItemPattern.ingot));
                IRecipeBuilder.blasting(this).create(
                        "oxide_" + MetalEnum.Iron.getName() + "_to_ingot",
                        MetalEnum.Iron.getItemTag(ItemPattern.metal_oxide),
                        MetalEnum.Iron.getStack(ItemPattern.ingot));
            }
        };
    }

    @Override
    public Set<MaterialInterface<?>> getSourceMaterials() {
        Set<MaterialInterface<?>> sources = new LinkedHashSet<>();
        sources.add(MetalEnum.Copper);
        sources.add(MetalEnum.Iron);

        return sources;
    }
}
