package igteam.api.materials.data.metal.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.pattern.ItemFamily;
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

public class MaterialMetalGold extends MaterialBaseMetal {

    public MaterialMetalGold() {
        super("gold");
        initializeColorMap((p) -> 0xFFD700);
    }
    @Override
    public boolean hasCrystal() {return false;}

    @Override
    public boolean isNative() {
        return true;
    }

    @Override
    public boolean hasExistingImplementation(){
        return true;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.extraction) {
            @Override
            protected void describe() {
                TagKey<Item> t = ItemTags.makeWrapperTag("forge:ores/gold");
                IRecipeBuilder.cutting(this)
                        .create(t, new FluidTagInput(FluidTags.WATER, 80),
                                StoneEnum.Stone.getStack(ItemFamily.ore_chunk, getParentMaterial()));

                IRecipeBuilder.bloomery(this).create(
                        "crushed_ore_" + getName() + "_to_ingot",
                        getParentMaterial().getItemTag(ItemFamily.crushed_ore), 2,
                        MetalEnum.Gold.getStack(ItemFamily.ingot),
                        120);
            }
        };

    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.GOLD)
        ));
    }

    @Override
    public ArrayList<MaterialInterface<? extends MaterialBaseMineral>> getSourceMinerals() {
        ArrayList<MaterialInterface<? extends MaterialBaseMineral>> lst = new ArrayList<>();
        return lst;
    }
}
