package com.igteam.immersivegeology.core.material.data.types;

import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.helper.HazardTypes;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.flags.MaterialFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MaterialChemical extends GeologyMaterial {

    public MaterialChemical(MaterialInterface<?>... slurryMetals) {
        super();
        addFlags(BlockCategoryFlags.SLURRY, BlockCategoryFlags.CLOUDY_SLURRY, BlockCategoryFlags.FLUID, MaterialFlags.IS_CHEMICAL);
		slurry_material_set.addAll(List.of(slurryMetals));
    }

    @Override
    public ResourceLocation getTextureLocation(IFlagType<?> flag) {
        // As this should always be a default stone we use the id minecraft and default it to whatever it is.
        // If we want to add support for other mods this will need to change
        return new ResourceLocation("minecraft", "block/"+getName());
    }

	private final HashSet<MaterialInterface<?>> slurry_material_set = new HashSet<>();

	public boolean hasSlurryWith(MaterialInterface<?> material)
	{
		return slurry_material_set.contains(material);
	}

	public boolean hasComplexNamingScheme()
	{
		return false;
	}

	@Override
	public void setupRecipeStages()
	{

	}
}
