package com.igteam.immersivegeology.core.material.data.mineral;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.CrystalFamily;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialMolybenite extends MaterialMineral {

    public MaterialMolybenite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        this.acceptableStoneTypes.add(StoneFormation.METAMORPHIC);

        // TODO Banished to the Nether! ~UnSchtalch
        CONFIG = new MineralConfig(12,70,1,-64,82,1000, 0.5,false,Optional.of(Biomes.IS_HOT));
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0xb3cbe4));
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return CrystalFamily.HEXAGONAL;
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Molybdenum));
    }
}
