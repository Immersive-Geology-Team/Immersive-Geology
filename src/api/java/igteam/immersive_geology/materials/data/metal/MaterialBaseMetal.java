package igteam.immersive_geology.materials.data.metal;

import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.data.BlockModelProvider;
import net.minecraft.item.Rarity;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public abstract class MaterialBaseMetal extends MaterialBase {

    public MaterialBaseMetal(String name) {
        super(name);
    }

    public boolean isNative() {return false;}

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON; //setup default
    }

    protected void setupProcessingStages(){
        super.setupProcessingStages();

        if(isNative()) {
            new IGProcessingStage(this, "Initial Crafting") {
                @Override
                protected void describe() {
                    for (MaterialInterface<?> stone : StoneEnum.values()) {
                        IRecipeBuilder.separating(this).create(
                                getParentMaterial().getItemTag(ItemPattern.dirty_crushed_ore, stone.get()),//input
                                getParentMaterial().getStack(ItemPattern.crushed_ore),
                                stone.getStack(ItemPattern.stone_bit)
                        );

                        IRecipeBuilder.crafting(this)
                                .shapeless(stone.getItem(ItemPattern.dirty_crushed_ore, getParentMaterial()), 1, getItemTag(ItemPattern.ore_chunk, stone.get()), getItemTag(ItemPattern.ore_chunk, stone.get()))
                                .finializeRecipe("crush_ore_chunks", "has_chunk", getItemTag(ItemPattern.ore_chunk, stone.get()));
                    }
                }
            };

            //Also note Osmium is a Native metal, so yeah I don't think that'd work for it... May need to hardcode the Bloomery recipes, it may just be better to do so

            if (!hasExistingImplementation()) { //TODO Fix this, essentially when something has an existing implementation EG: Silver, it already has an Ingot, so an Ingot pattern will not retrieve the ingot, we'd need to use a tag for output.
                new IGProcessingStage(this, "Roasting for Native Metals") {
                    @Override
                    protected void describe() {
                        IRecipeBuilder.bloomery(this).create("native_" + getName() + "_to_ingot", getParentMaterial().getStack(ItemPattern.crushed_ore, 2), getParentMaterial().getStack(ItemPattern.ingot), 2000);
                    }
                };
            }
        }
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        ResourceLocation returnTexture = new ResourceLocation(IGApi.MODID, "block/colored/" + this.name + "/" + pattern.getName()); //This doesn't exist so if it's parsed it'll Trigger GreyScale Textures


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
        this.useColorTint = true;

        if(pattern instanceof BlockPattern){
            BlockPattern b = (BlockPattern) pattern;
            switch(b) {
                case ore: return new ResourceLocation(IGApi.MODID, "block/greyscale/rock/ore_bearing/vanilla/vanilla_normal");
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
        return null;
    }

    @Override
    protected boolean hasStorageBlock() {
        return !hasExistingImplementation();
    }

    @Override
    protected boolean hasStairs() {
        return false;
    }

    @Override
    protected boolean hasOreBlock() {
        return isNative();
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
        return !hasExistingImplementation();
    }

    @Override
    protected boolean hasWire() {
        return false;
    }

    @Override
    protected boolean hasGear() {
        return true;
    }

    @Override
    protected boolean hasRod() {
        return true;
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
        return true;
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
        return !hasExistingImplementation();
    }

    @Override
    protected boolean hasNugget() {
        return hasIngot();
    }

    @Override
    protected boolean hasCrystal() {
        return true;
    }

    @Override
    protected boolean hasOreBit() {
        return isNative();
    }

    @Override
    protected boolean hasOreChunk() {
        return isNative();
    }

    @Override
    protected boolean hasStoneBit() {
        return false;
    }

    @Override
    protected boolean hasCrushedOre() {
        return isNative();
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
        return true;
    }

    @Override
    protected boolean hasDirtyCrushedOre() {
        return isNative();
    }

    @Override
    public boolean generateOreFor(MaterialInterface m) {
        if(m instanceof StoneEnum){
            StoneEnum s = (StoneEnum) m;
            switch(s){
                case Stone:
                    return !hasExistingImplementation();
                case Granite:
                    return isNative();
            };
        }
        return false;
    }

    @Override
    public boolean isFluidPortable(ItemPattern pattern){
        return false;
    }
}