package igteam.api.materials.data.stone.variants;

import igteam.api.materials.data.stone.MaterialBaseStone;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.Items;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialDefaultStone extends MaterialBaseStone {

    public MaterialDefaultStone() {
        super("stone");
        initializeColorMap((p) -> 0x8F8F8F);
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 3)
        ));
    }

    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.preparation) {
            @Override
            protected void describe() {
                if (hasStoneChunk() ){
                    IRecipeBuilder.crafting(this).shaped(
                                    Items.COBBLESTONE, 1,
                                    "ccc", "ccc", "ccc").setInputToCharacter('c', getItem(ItemFamily.stone_chunk))
                            .finializeRecipe("general_crafting", "has_stone_chunk", getItemTag(ItemFamily.stone_chunk));;
                }
            }
        };
    }

    @Override
    protected boolean hasDefaultBlock() {
        return false;
    }

    @Override
    public boolean hasExistingImplementation() {
        return true;
    }

    @Override
    public boolean generateOreFor(MaterialInterface m) {
        return getDimension().equals(MaterialSourceWorld.overworld);
    }
}
