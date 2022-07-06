package igteam.immersive_geology.materials.data.metal.variants;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import igteam.immersive_geology.materials.MineralEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.PeriodicTableElement;
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

public class MaterialMetalUranium extends MaterialBaseMetal {

    public MaterialMetalUranium() {
        super("uranium");
        initializeColorMap((p) -> 0x759068);
    }
    @Override
    public boolean hasMetalOxide () {return true;}

    @Override
    public boolean hasCompoundDust () {return true;}

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.ORTHORHOMBIC;
    }

    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.preparation) {
            @Override
            protected void describe() {

                ITag<Item> t = ItemTags.makeWrapperTag("forge:ores/uranium");

                IRecipeBuilder.cutting(this).create(
                        t,
                        new FluidTagInput(FluidTags.WATER, 80),
                        StoneEnum.Stone.getStack(ItemPattern.ore_chunk, MineralEnum.Uraninite.get(), 5)

                );
            }
        };
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.URANIUM)
        ));
    }
}
