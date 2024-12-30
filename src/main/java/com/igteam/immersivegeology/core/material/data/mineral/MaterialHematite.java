package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.client.helper.IGVeinTextureType;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.flags.ModFlags;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialHematite extends MaterialMineral {

    public MaterialHematite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_EXTRUSIVE);
        addFlags(ModFlags.TFC, MaterialFlags.EXISTING_IMPLEMENTATION);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        addExistingFlag(ModFlags.TFC, BlockCategoryFlags.ORE_BLOCK);
        // TODO disk like spawn type
        CONFIG = new MineralConfig(15,30,2,0,160,1000, 0.5,false,Optional.of(Biomes.IS_SPARSE_OVERWORLD));
    }

    @Override
    public boolean willTarnishOverTime()
    {
        return true;
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x4B2F2C));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Iron));
    }

    @Override
    public IGVeinTextureType getVeinType()
    {
        return IGVeinTextureType.LAYERED;
    }
}
