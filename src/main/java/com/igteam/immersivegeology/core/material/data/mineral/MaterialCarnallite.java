package com.igteam.immersivegeology.core.material.data.mineral;
            
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialMineral;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Biomes;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

public class MaterialCarnallite extends MaterialMineral {

    public MaterialCarnallite() {
        super();
        this.acceptableStoneTypes.add(StoneFormation.SEDIMENTARY);
        this.acceptableStoneTypes.add(StoneFormation.MINECRAFT_STONE);

        CONFIG = new MineralConfig(12,45,1,24,128,1350,0.5,false,Optional.of(Biomes.IS_SANDY));
    }

    @Override
    protected BiFunction<IFlagType<?>, Integer, Integer> materialColorFunction() {
        return ((p, i) -> (0x615964));
    }

    @Override
    public LinkedHashSet<MaterialInterface<?>> getSourceMaterials()
    {
        return new LinkedHashSet<>(Set.of(MetalEnum.Magnesium, MetalEnum.Titanium));
    }
}
