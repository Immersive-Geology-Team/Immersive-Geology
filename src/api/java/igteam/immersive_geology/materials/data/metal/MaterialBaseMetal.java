package igteam.immersive_geology.materials.data.metal;

import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import igteam.immersive_geology.processing.helper.IRecipeBuilder;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.RecipeMethod;

public class MaterialBaseMetal extends MaterialBase {

    public MaterialBaseMetal(String name) {
        super(name);
}

    @Override
    public ResourceLocation getTextureLocation() {
        return null;
    }

    @Override
    public int getColor() {
        return 0;
    }

    public boolean isNative() {return false;}

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON; //setup default
    }

    protected void setupProcessingStages(){
        super.setupProcessingStages();
    }

    @Override
    protected boolean hasStorageBlock() {
        return true;
    }

    @Override
    protected boolean hasStairs() {
        return true;
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
        return true;
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
        return false;
    }

    @Override
    protected boolean hasNugget() {
        return true;
    }

    @Override
    protected boolean hasCrystal() {
        return false;
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
        return false;
    }

    @Override
    protected boolean hasDirtyCrushedOre() {
        return isNative();
    }
}