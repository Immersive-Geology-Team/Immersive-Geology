package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.RotaryKilnLogic;

import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;

import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialGypsum extends MaterialMineral {

    public MaterialGypsum() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        removeMaterialFlags(ItemCategoryFlags.GRIT, ItemCategoryFlags.POWDER);
        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
        CONFIG = new MineralConfig(22,30,3,-64,80,700, 0.5,false,Optional.empty(), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff90AB8C));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.MONOCLINIC;
    }
    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Calcium));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.decompose(this, IGStageDesignation.PREPARATION).create(
                "crushed_ore_"+getName()+"_to_metal_oxide", MetalEnum.Calcium.getStack(ItemCategoryFlags.METAL_OXIDE),
                getItemTag(ItemCategoryFlags.CRUSHED_ORE), 1, 300).setHVHeat();
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.09667969f;
    }
}
