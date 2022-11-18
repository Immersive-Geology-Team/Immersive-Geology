package igteam.api.materials.data.mineral.variants;

import igteam.api.materials.MetalEnum;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.Rarity;
import net.minecraft.world.item.Rarity;

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
        new IGProcessingStage(this, IGStageDesignation.extraction){
            @Override
            protected void describe() {
                IRecipeBuilder.crushing(this).create( "slag_" +getName() + "_to_dust",
                        getItemTag(ItemFamily.slag),
                        getStack(ItemFamily.dust), 3000, 200);
                IRecipeBuilder.separating(this).create(
                        getParentMaterial().getItemTag(ItemFamily.dust),
                        MetalEnum.Iron.getStack(ItemFamily.metal_oxide),
                        MetalEnum.Copper.getStack(ItemFamily.metal_oxide));
            }
        };
        new IGProcessingStage(this, IGStageDesignation.roasting){
            @Override
            protected void describe() {
                IRecipeBuilder.roast(this).create(
                        "mineral_" + getName() + "_to_slag",
                        getParentMaterial().getItemTag(ItemFamily.crushed_ore), 1,
                        getParentMaterial().getStack(ItemFamily.slag), 1000, 1);
            }
        };
        new IGProcessingStage(this, IGStageDesignation.blasting) {
            @Override
            protected void describe() {

                IRecipeBuilder.blasting(this).create(
                        "oxide_" + MetalEnum.Copper.getName() + "_to_ingot",
                        MetalEnum.Copper.getItemTag(ItemFamily.metal_oxide),
                        MetalEnum.Copper.getStack(ItemFamily.ingot));
                IRecipeBuilder.blasting(this).create(
                        "oxide_" + MetalEnum.Iron.getName() + "_to_ingot",
                        MetalEnum.Iron.getItemTag(ItemFamily.metal_oxide),
                        MetalEnum.Iron.getStack(ItemFamily.ingot));
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
