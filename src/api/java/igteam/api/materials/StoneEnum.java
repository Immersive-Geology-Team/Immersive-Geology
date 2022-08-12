package igteam.api.materials;

import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.data.stone.MaterialBaseStone;
import igteam.api.materials.data.stone.variants.*;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.materials.helper.CrystalFamily;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.fluids.FluidStack;
import igteam.api.processing.IGProcessingStage;

import java.util.Set;

public enum StoneEnum implements MaterialInterface<MaterialBaseStone> {
    Stone(new MaterialDefaultStone()),
    Granite(new MaterialGranite()),
    Netherrack(new MaterialNetherrack()),
    Blackstone(new MaterialBlackstone()),
    End_stone(new MaterialEndstone()); //Needs underscore to generate correctly

    //TODO Add Endstone stone type ~Muddykat

    private final MaterialBaseStone material;

    StoneEnum(MaterialBaseStone m){
        this.material = m;
    }

    @Override
    public MaterialBaseStone instance() {
        return material;
    }

    @Override
    public IFeatureConfig getGenerationConfig() {
        return null;
    }

    @Override
    public boolean isFluidPortable(ItemPattern bucket) {
        return false;
    }
}
