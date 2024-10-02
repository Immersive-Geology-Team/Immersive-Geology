package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.StoneFormation;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class MaterialChemical extends GeologyMaterial {

    public MaterialChemical(MetalEnum... slurryMetals) {
        super();
        addFlags(BlockCategoryFlags.SLURRY, BlockCategoryFlags.FLUID, MaterialFlags.IS_CHEMICAL);
		slurryMetalSet.addAll(List.of(slurryMetals));
    }

    @Override
    public ResourceLocation getTextureLocation(IFlagType<?> flag) {
        // As this should always be a default stone we use the id minecraft and default it to whatever it is.
        // If we want to add support for other mods this will need to change
        return new ResourceLocation("minecraft", "block/"+getName());
    }

	private final EnumSet<MetalEnum> slurryMetalSet = EnumSet.noneOf(MetalEnum.class);

	public boolean hasSlurryMetal(MetalEnum metal)
	{
		return slurryMetalSet.contains(metal);
	}

	@Override
	public void setupRecipeStages()
	{

	}
}
