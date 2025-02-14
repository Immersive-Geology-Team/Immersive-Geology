package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.IGStageDesignation;
import com.igteam.immersivegeology.core.material.helper.material.recipe.helper.IGMethodBuilder;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialPyrolusite extends MaterialMineral {

    public MaterialPyrolusite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        addFlags(ItemCategoryFlags.PELLET);
        CONFIG = new MineralConfig(10,40,2,-64,112,350, 0.5,false,Optional.of(Biomes.IS_WET), IGGenerationType.DEFAULT);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xc68f39));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.TETRAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getDerivedMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Manganese, MetalEnum.Iron));
    }
    @Override
    public void setupRecipeStages()
    {
        super.setupRecipeStages();
        IGMethodBuilder.blasting(this, IGStageDesignation.EXTRACTION).create("crushed_ore_"+getName()+"_to_ingot",
                getItemTag(ItemCategoryFlags.CRUSHED_ORE),
                getPrimaryProduct().getStack(ItemCategoryFlags.INGOT), 900);

        IGMethodBuilder.separating(this, IGStageDesignation.EXTRACTION).create(getItemTag(ItemCategoryFlags.POWDER),
                MetalEnum.Manganese.getStack(ItemCategoryFlags.METAL_OXIDE),
                MetalEnum.Iron.getStack(ItemCategoryFlags.METAL_OXIDE), 0.075f, 200, 1000);
    }

}
