package igteam.immersive_geology.materials.data.metal.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.helper.PeriodicTableElement.ElementProportion;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGStageDesignation;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialMetalCopper extends MaterialBaseMetal {

    public MaterialMetalCopper() {
        super("copper");
        initializeColorMap((p) -> 0xe39919);
    }

    @Override
    public boolean isNative() {
        return true;
    }

    @Override
    protected boolean hasMetalOxide() {
        return true;
    }
    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
    public LinkedHashSet<ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new ElementProportion(PeriodicTableElement.COPPER)
        ));
    }

    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.preparation) {
            @Override
            protected void describe() {

                ITag<Item> t = ItemTags.makeWrapperTag("forge:ores/copper");

                IRecipeBuilder.cutting(this).create(
                        t,
                        new FluidTagInput(FluidTags.WATER, 80),
                        StoneEnum.Stone.getStack(ItemPattern.ore_chunk, getParentMaterial(), 5)

                );
            }
        };

        new IGProcessingStage(this, IGStageDesignation.extraction) {
            @Override
            protected void describe() {
                IRecipeBuilder.bloomery(this).create(
                        "crushed_ore_" + getName() + "_to_ingot",
                        getParentMaterial().getStack(ItemPattern.crushed_ore, 2),
                        MetalEnum.Copper.getStack(ItemPattern.ingot),
                        120);
            }
        };
    }

}
