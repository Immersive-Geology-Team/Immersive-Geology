package igteam.immersive_geology.materials.data.mineral.variants;

import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.Rarity;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMineralGalena extends MaterialBaseMineral {

    public MaterialMineralGalena() {
        super("galena");
        initializeColorMap((p) -> 0x857F83);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.CUBIC;
    }

    @Override
    public boolean hasSlag() {return true;}
    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, "Roasting Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.roast(this).create("mineral_" + getName() + "_to_slag",
                        getParentMaterial().getStack(ItemPattern.crushed_ore), getParentMaterial().getStack(ItemPattern.slag), 1000, 1);

            }
        };
        new IGProcessingStage(this, "Blasting Stage") {
            @Override
            protected void describe() {
                IRecipeBuilder.blasting(this).create(
                        "slag_" + getName() + "_to_ingot",
                        getParentMaterial().getItemTag(ItemPattern.slag),
                        MetalEnum.Lead.getStack(ItemPattern.ingot, 1));
            }
        };

        //TODO Arc Furnace smelting slag with zinc to get Lead AND Silver


    }
    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.LEAD),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SULFUR)
            )
        );
    }

    @Override
    public Rarity getRarity()
    {
        return Rarity.COMMON;
    }

}
