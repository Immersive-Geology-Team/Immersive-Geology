package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.common.block.multiblocks.logic.RotaryKilnLogic;
import com.igteam.immersivegeology.common.world.features.helper.noise.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGRecipeChain;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialIlmenite extends MaterialMineral {

    protected IGRecipeChain becher_process = new IGRecipeChain(this, "Becher process", 0);

    public MaterialIlmenite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        addFlags(ItemCategoryFlags.SLAG);
        addFlags(ItemCategoryFlags.POWDERED_SLAG);

        CONFIG = new MineralConfig(33,40,2,5,140,2000, 0.5,false,Optional.of(Biomes.IS_MOUNTAIN), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xff4A3E3E));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(List.of(MetalEnum.Iron, MetalEnum.Titanium));
    }
    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();

        // Becher process, simplified

        IGMethodBuilder.decompose(this, IGStageDesignation.PURIFICATION).create(
                "crushed_ore_"+getName()+"_to_slag", getStack( ItemCategoryFlags.SLAG, 1),
                getItemTag(ItemCategoryFlags.CRUSHED_ORE), 1, 300).setHVHeat().addToTree(becher_process);

        IGMethodBuilder.pulverization(this, IGStageDesignation.PURIFICATION).create(
                ItemCategoryFlags.SLAG,ItemCategoryFlags.POWDERED_SLAG).addToTree(becher_process);

        IGMethodBuilder.separating(this, IGStageDesignation.EXTRACTION).create(
                getItemTag(ItemCategoryFlags.POWDERED_SLAG),
                getPrimaryProduct().getStack(ItemCategoryFlags.METAL_OXIDE),
                getSecondaryProduct().getStack(ItemCategoryFlags.METAL_OXIDE), 0.5f, 300, 250)
                .addToTree(becher_process);

        //Important - NO WATER in reactions!

    }

    @Override
    public Set<IGRecipeChain> getRecipeChains()
    {
        return Set.of(becher_process);
    }

    @Override
    public float getNoiseProbability()
    {
        return 0.08856201f;
    }
}