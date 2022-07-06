package igteam.immersive_geology.materials.data.metal.variants;

import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
import igteam.immersive_geology.materials.helper.PeriodicTableElement.ElementProportion;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGStageDesignation;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import igteam.immersive_geology.processing.methods.IGHydrojetMethod;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;

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
                IRecipeBuilder.cutting(this).create(ItemTags.GOLD_ORES, new FluidTagInput(FluidTags.WATER, 80), StoneEnum.Stone.getStack(ItemPattern.ore_chunk));
            }
        };
    }

    @Override
    public LinkedHashSet<ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new ElementProportion(PeriodicTableElement.GOLD)
        ));
    }

    protected void setupProcessingStages() {
        super.setupProcessingStages();
        new IGProcessingStage(this, IGStageDesignation.extraction) {
            @Override
            protected void describe() {
                IRecipeBuilder.bloomery(this).create(
                        "crushed_ore_" + getName() + "_to_ingot",
                        getParentMaterial().getStack(ItemPattern.crushed_ore, 2),
                        MetalEnum.Gold.getStack(ItemPattern.ingot),
                        120);
            }
        };
    }

}
