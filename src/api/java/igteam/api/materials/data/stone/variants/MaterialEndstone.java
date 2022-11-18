package igteam.api.materials.data.stone.variants;

import igteam.api.IGApi;
import igteam.api.materials.data.stone.MaterialBaseStone;
import igteam.api.materials.helper.MaterialSourceWorld;
import igteam.api.materials.helper.PeriodicTableElement;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockFamily;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashSet;

public class MaterialEndstone extends MaterialBaseStone {
    public MaterialEndstone() {
        super("end_stone");

        initializeColorMap((p) -> (0xC9C288));
    }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements() {
        return new LinkedHashSet<>(Collections.singletonList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON)
        ));
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        if(pattern instanceof BlockFamily){
            BlockFamily b = (BlockFamily) pattern;
            switch(b) {
                case block: case ore: return new ResourceLocation("minecraft", "block/end_stone");
                default: return new ResourceLocation("minecraft", "block/end_stone");
            }
        }

        if(pattern instanceof ItemFamily){
            ItemFamily i = (ItemFamily) pattern;
            switch(i) {
                case ore_chunk: case stone_chunk: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_chunk");
                case ore_bit: case stone_bit: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_bit");
                case dirty_crushed_ore: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/crushed_stone");
                default: return new ResourceLocation("minecraft", "block/end_stone");
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
                                    Items.END_STONE, 1,
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
        return getDimension().equals(MaterialSourceWorld.end);
    }
}
