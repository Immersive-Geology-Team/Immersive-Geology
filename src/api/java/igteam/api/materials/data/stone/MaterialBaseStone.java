package igteam.api.materials.data.stone;

import igteam.api.IGApi;
import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.pattern.BlockFamily;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.materials.pattern.MaterialPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGStageDesignation;
import igteam.api.processing.helper.IRecipeBuilder;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;

public abstract class MaterialBaseStone extends MaterialBase {
    public MaterialBaseStone(String name) {
        super(name);
    }

    @Override
    public int getColor(MaterialPattern p)  {
        return (p != BlockFamily.ore && p != ItemFamily.block_item && p != BlockFamily.block) ? colorFunction.apply(p) : 0xFFFFFF;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    protected boolean hasStorageBlock() {
        return false;
    }

    @Override
    protected boolean hasStairs() {
        return false;
    }

    @Override
    protected boolean hasOreBlock() {
        return false;
    }

    @Override
    protected boolean hasGeodeBlock() {
        return false;
    }

    @Override
    protected boolean hasDefaultBlock() {
        return !hasExistingImplementation();
    }

    @Override
    protected boolean hasSlab() {
        return false;
    }

    @Override
    protected boolean hasIngot() {
        return false;
    }

    @Override
    protected boolean hasWire() {
        return false;
    }

    @Override
    protected boolean hasGear() {
        return false;
    }

    @Override
    protected boolean hasRod() {
        return false;
    }

    @Override
    protected boolean isMachine() {
        return false;
    }

    @Override
    protected boolean isSlurry() {
        return false;
    }

    @Override
    protected boolean isFluid() {
        return false;
    }

    @Override
    protected boolean hasClay() {
        return false;
    }

    @Override
    protected boolean hasDust() {
        return false;
    }

    @Override
    protected boolean hasFuel() {
        return false;
    }

    @Override
    protected boolean hasSlag() {
        return false;
    }

    @Override
    protected boolean hasPlate() {
        return false;
    }

    @Override
    protected boolean hasNugget() {
        return false;
    }

    @Override
    protected boolean hasCrystal() {
        return false;
    }

    @Override
    protected boolean hasOreBit() {
        return false;
    }

    @Override
    protected boolean hasOreChunk() {
        return false;
    }

    @Override
    protected boolean hasStoneBit() {
        return true;
    }

    @Override
    protected boolean hasCrushedOre() {
        return false;
    }

    @Override
    protected boolean hasMetalOxide() {
        return false;
    }

    @Override
    protected boolean hasStoneChunk() {
        return true;
    }

    @Override
    protected boolean hasCompoundDust() {
        return false;
    }

    @Override
    protected boolean hasDirtyCrushedOre() {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        if(pattern instanceof BlockFamily){
            BlockFamily b = (BlockFamily) pattern;
            switch(b) {
                case block: case ore: return new ResourceLocation("minecraft", "block/stone");
                default: return new ResourceLocation("minecraft", "block/blackstone");
            }
        }

        if(pattern instanceof ItemFamily){
            ItemFamily i = (ItemFamily) pattern;
            switch(i) {
                case ore_chunk: case stone_chunk: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_chunk");
                case ore_bit: case stone_bit: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/rock_bit");
                case dirty_crushed_ore: return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/crushed_stone");
                default: return new ResourceLocation("minecraft", "block/stone");
            }
        }
        return null;
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern, int subtype) {
        return null;
    }

    @Override
    public boolean isFluidPortable(ItemFamily pattern){
        return false;
    }

    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this, IGStageDesignation.preparation) {
            @Override
            protected void describe() {
                if (hasStoneBit() && hasStoneChunk() ){
                    IRecipeBuilder.crafting(this).shaped(
                           getItem(ItemFamily.stone_chunk), 1,
                            "ccc", "ccc", "ccc").setInputToCharacter('c', getItem(ItemFamily.stone_bit))
                            .finializeRecipe("general_crafting", "has_bit", getItemTag(ItemFamily.stone_bit));;
                }
            }
        };
    }

}
