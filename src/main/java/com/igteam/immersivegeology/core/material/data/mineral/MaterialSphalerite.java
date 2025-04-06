package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialSulphideMineral;
import com.igteam.immersivegeology.core.material.helper.flags.*;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.tags.BiomeTags;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class MaterialSphalerite extends MaterialSulphideMineral
{

    public MaterialSphalerite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);
        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
        addFlags(ItemCategoryFlags.SLAG);
		addFlags(ItemCategoryFlags.PELLET);
		addFlags(ItemCategoryFlags.POWDERED_SLAG);

		CONFIG = new MineralConfig(21,45,3,0,140,800, 0.5,false,Optional.of(BiomeTags.IS_NETHER), IGGenerationType.TUBE);
	}

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x6F8070));
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Zinc, MetalEnum.Iron));
    }

    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        IGMethodBuilder.bloomery(this, IGStageDesignation.REFINEMENT).create(ItemCategoryFlags.CRUSHED_ORE,
                2, ItemCategoryFlags.INGOT, 1, 400);

        IGMethodBuilder.roast(this, IGStageDesignation.PREPARATION).create(
                ItemCategoryFlags.CRUSHED_ORE, 1,
                ItemCategoryFlags.SLAG, 1, 800, 250);

        IGMethodBuilder.pulverization(this, IGStageDesignation.PREPARATION).create(
                ItemCategoryFlags.SLAG,
                ItemCategoryFlags.POWDERED_SLAG);

        IGMethodBuilder.separating(this, IGStageDesignation.PURIFICATION).create(
                getItemTag(ItemCategoryFlags.POWDERED_SLAG),
                MetalEnum.Zinc.getStack(ItemCategoryFlags.METAL_OXIDE),
                MetalEnum.Iron.getStack(ItemCategoryFlags.METAL_OXIDE),
                0.75f, 200, 1000);
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.203125f;
    }
}
