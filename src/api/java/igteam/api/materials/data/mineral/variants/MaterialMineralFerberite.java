package igteam.api.materials.data.mineral.variants;

import igteam.api.materials.MetalEnum;
import igteam.api.materials.helper.MaterialSourceWorld;
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

public class MaterialMineralFerberite extends MaterialBaseMineral {

    public MaterialMineralFerberite() {
        super("ferberite");
        initializeColorMap((p) -> 0x2688D1);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }

    @Override
    public boolean hasSlag()  {return true;}

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.extraction) {
            @Override
            protected void describe() {
                IRecipeBuilder.blasting(this).create(
                        "crushed_ore_" + getName() + "_to_slag",
                        getItemTag(ItemFamily.crushed_ore),
                        MetalEnum.Iron.getStack(ItemFamily.ingot),
                        getStack(ItemFamily.slag));

                IRecipeBuilder.crushing(this).create(
                        "slag_" + getName() + "_to_dust",
                        getItemTag(ItemFamily.slag),
                        getStack(ItemFamily.dust),
                        10000, 200);

                IRecipeBuilder.separating(this).create(
                        getItemTag(ItemFamily.dust),
                        MetalEnum.Tungsten.getStack(ItemFamily.metal_oxide),
                        MetalEnum.Iron.getStack(ItemFamily.dust));
            }
        };

        new IGProcessingStage(this, IGStageDesignation.blasting){
            @Override
            protected void describe() {
                //To be fair, it should be dust, but I'd like not to bother here
                IRecipeBuilder.blasting(this).create(
                        "metal_oxide_" + MetalEnum.Tungsten.getName() + "_to_ingot",
                        MetalEnum.Tungsten.getItemTag(ItemFamily.metal_oxide),
                        MetalEnum.Tungsten.getStack(ItemFamily.ingot));
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.IRON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.TUNGSTEN),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 4)
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
        sources.add(MetalEnum.Iron);
        sources.add(MetalEnum.Tungsten);

        return sources;
    }

    @Override
    public MaterialSourceWorld getDimension() {
        return MaterialSourceWorld.end;
    }
}
