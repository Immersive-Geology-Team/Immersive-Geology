package igteam.immersive_geology.materials.data.mineral;

import blusunrize.immersiveengineering.api.IEApi;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.data.stone.MaterialBaseStone;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IGStageDesignation;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

import static java.util.Arrays.asList;

public abstract class MaterialBaseMineral extends MaterialBase {
    public MaterialBaseMineral(String name) {
        super(name);
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
        return true;
    }

    @Override
    protected boolean hasGeodeBlock() {
        return false;
    }

    @Override
    protected boolean hasDefaultBlock() {
        return false;
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
        return hasCrystal();
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
        return true;
    }

    @Override
    protected boolean hasOreBit() {
        return true;
    }

    @Override
    protected boolean hasOreChunk() {
        return true;
    }

    @Override
    protected boolean hasStoneBit() {
        return false;
    }

    @Override
    protected boolean hasCrushedOre() {
        return true;
    }

    @Override
    protected boolean hasMetalOxide() {
        return false;
    }

    @Override
    protected boolean hasStoneChunk() {
        return false;
    }

    @Override
    protected boolean hasCompoundDust() {
        return false;
    }

    @Override
    protected boolean hasDirtyCrushedOre() {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        ResourceLocation returnTexture = new ResourceLocation(IGApi.MODID, "null"); //This doesn't exist so if it's parsed it'll Trigger GreyScale Textures

        if (pattern instanceof BlockPattern) {
            returnTexture = new ResourceLocation(IGApi.MODID, "block/colored/" + this.name + "/" + pattern.getName());
        }

        if (pattern instanceof ItemPattern) {
            returnTexture = new ResourceLocation(IGApi.MODID, "item/colored/" + this.name + "/" + pattern.getName());
        }
        boolean exists = StaticTemplateManager.EXISTING_HELPER.exists(new ResourceLocation(IGApi.MODID, "textures/" + returnTexture.getPath() + ".png"), ResourcePackType.CLIENT_RESOURCES);
        return exists ? returnTexture : greyScaleTextures(pattern);
    }

    private ResourceLocation greyScaleTextures(MaterialPattern pattern) {
        if(pattern instanceof BlockPattern){
            BlockPattern b = (BlockPattern) pattern;
            switch(b) {
                case ore:
                    String ore_overlay = getCrystalFamily() != null ? getCrystalFamily().getName() : "vanilla_normal";
                    return new ResourceLocation(IGApi.MODID, "block/greyscale/rock/ore_bearing/vanilla/" + ore_overlay);
                case storage: return new ResourceLocation(IGApi.MODID, "block/greyscale/metal/storage");
                case geode: return new ResourceLocation(IGApi.MODID, "block/greyscale/stone/geode");
                default: return new ResourceLocation(IGApi.MODID, "block/greyscale/stone/cobble");
            }
        }

        if(pattern instanceof ItemPattern){
            ItemPattern i = (ItemPattern) pattern;
            switch(i) {
                case ore_chunk:
                    String chunk_vein_name = "rock_chunk_vein" + (getCrystalFamily() != null ? "_" + getCrystalFamily().getName() : "");
                    ResourceLocation chunkLoc = new ResourceLocation(IGApi.MODID, "item/greyscale/rock/ore_chunk/" + chunk_vein_name);
                    return chunkLoc;
                case stone_chunk:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/ore_chunk/rock_chunk_vein");
                case ore_bit: case stone_bit:
                    String bit_vein_name = "rock_bit_vein" + (getCrystalFamily() != null ? "_" + getCrystalFamily().getName() : "");
                    ResourceLocation bitLoc = new ResourceLocation(IGApi.MODID, "item/greyscale/rock/ore_bit/" + bit_vein_name);
                    return bitLoc;
                case dirty_crushed_ore: case crushed_ore:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/crushed_ore");
                case clay: case slag:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/rock/" + i.getName());
                case dust: case gear: case ingot: case nugget: case plate: case rod: case wire: case metal_oxide: case compound_dust:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/metal/" + i.getName());
                case crystal:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/crystal/raw_crystal_" + getCrystalFamily().getName());
                default:
                    return new ResourceLocation(IGApi.MODID, "item/greyscale/" + i.getName());
            }
        }
        return null;
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern, int subtype) {
        IGApi.getNewLogger().warn("Used a Subtype (" + String.valueOf(subtype) + ") for Material Pattern: " + pattern.getName() + " made of " + getName() + " | No Handling is setup for such.");
        return getTextureLocation(pattern);
    }

    @Override
    public boolean isFluidPortable(ItemPattern pattern){
        return false;
    }

    @Override
    protected void setupProcessingStages() {
        super.setupProcessingStages();

        new IGProcessingStage(this,IGStageDesignation.extraction) {
            @Override
            protected void describe() {
                for (MaterialInterface<?> stone : StoneEnum.values()) {
                    IRecipeBuilder.separating(this).create(
                            getParentMaterial().getItemTag(ItemPattern.dirty_crushed_ore, stone.get()),//result
                            getParentMaterial().getStack(ItemPattern.crushed_ore), //input
                            stone.getStack(ItemPattern.stone_bit));

                    IRecipeBuilder.crafting(this)
                            .shapeless(stone.getItem(ItemPattern.dirty_crushed_ore, getParentMaterial()), 1, getItemTag(ItemPattern.ore_chunk, stone.get()), getItemTag(ItemPattern.ore_chunk, stone.get()))
                            .finializeRecipe("crush_ore_chunks", "has_chunk", getItemTag(ItemPattern.ore_chunk, stone.get()));

                    IRecipeBuilder.crushing(this).create(getName() + "_oreblock_to_chunk",
                            getItemTag(BlockPattern.ore, stone.get()),  stone.getStack(ItemPattern.ore_chunk, getParentMaterial(), 4),1000, 500);

                    IRecipeBuilder.crushing(this).create(getName() + "_orechunk_to_dirtycrush",
                            getItemTag(ItemPattern.ore_chunk, stone.get()),  stone.getStack(ItemPattern.dirty_crushed_ore, getParentMaterial()),1000, 500);
                }
            }
        };

        new IGProcessingStage(this, IGStageDesignation.preparation){
            @Override
            protected void describe() {
                if (hasDust() && hasCrystal()) {
                    IRecipeBuilder.crushing(this).create(getName() + "_crystal_to_grit",
                            getItemTag(ItemPattern.crystal),  getStack(ItemPattern.dust),1000, 500);
                }
            }
        };
    }
}
