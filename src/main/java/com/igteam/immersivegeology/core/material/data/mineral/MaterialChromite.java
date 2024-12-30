package com.igteam.immersivegeology.core.material.data.mineral;
            
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MaterialChromite extends MaterialMineral {

    public MaterialChromite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.IGNEOUS_INTRUSIVE);
        this.acceptableStoneTypes.add(StoneFormation.METAMORPHIC);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        CONFIG = new MineralConfig(12,45,1,-64,24,1350,0.5,false,Optional.of(Biomes.IS_SPARSE_OVERWORLD));
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x615964));
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Chromium, MetalEnum.Iron));
    }
}
