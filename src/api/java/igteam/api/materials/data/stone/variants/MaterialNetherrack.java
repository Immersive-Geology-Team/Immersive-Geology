package igteam.api.materials.data.stone.variants;

import igteam.api.IGApi;
import igteam.api.materials.data.stone.MaterialBaseStone;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashSet;

public class MaterialNetherrack extends MaterialBaseStone {
    public MaterialNetherrack() {
        super("netherrack");
        initializeColorMap((p) -> (0x5C221D));
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Collections.singletonList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON)
        ));
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        if(pattern instanceof BlockPattern){
            BlockPattern b = (BlockPattern) pattern;
            switch(b) {
                case block: case ore: return new ResourceLocation("minecraft", "block/netherrack");
                default: return new ResourceLocation("minecraft", "block/netherrack");
            }
        }

        if(pattern instanceof ItemPattern){
            ItemPattern i = (ItemPattern) pattern;
            switch(i) {
                case ore_chunk: case stone_chunk: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_chunk");
                case ore_bit: case stone_bit: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_bit");
                case dirty_crushed_ore: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/crushed_stone");
                default: return new ResourceLocation("minecraft", "block/netherrack");
            }
        }
        return null;
    }


    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.preparation) {
            @Override
            protected void describe() {
                if (hasStoneChunk() ){
                    IRecipeBuilder.crafting(this).shaped(
                                    Items.NETHERRACK, 1,
                                    "ccc", "ccc", "ccc").setInputToCharacter('c', getItem(ItemPattern.stone_chunk))
                            .finializeRecipe("general_crafting", "has_stone_chunk", getItemTag(ItemPattern.stone_chunk));
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
        return getDimension().equals(MaterialSourceWorld.nether);
    }
}
