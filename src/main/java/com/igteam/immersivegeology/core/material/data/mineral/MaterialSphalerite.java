package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.common.world.features.helper.IGGenerationType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.data.types.MaterialSulphideMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialSphalerite extends MaterialSulphideMineral
{

    public MaterialSphalerite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);
        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);
        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);

        CONFIG = new MineralConfig(10,45,3,0,140,800, 0.5,false,Optional.of(BiomeTags.IS_NETHER), IGGenerationType.TUBE);
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x6F8070));
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Zinc, MetalEnum.Iron));
    }
}
