package igteam.api.materials.data.metal.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.materials.MineralEnum;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

import java.util.ArrayList;
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
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.COPPER)
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
                        getParentMaterial().getItemTag(ItemPattern.crushed_ore), 2,
                        MetalEnum.Copper.getStack(ItemPattern.ingot),
                        120);
            }
        };
    }

    @Override
    public ArrayList<MaterialInterface<? extends MaterialBaseMineral>> getSourceMinerals() {
        ArrayList<MaterialInterface<? extends MaterialBaseMineral>> lst = new ArrayList<>();
        lst.add(MineralEnum.Cuprite);
        lst.add(MineralEnum.Chalcopyrite);
        lst.add(MineralEnum.Chalcocite);

        return lst;
    }

}
