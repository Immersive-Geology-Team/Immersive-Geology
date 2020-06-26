package com.igteam.immersivegeology.common.world.help;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;

import static com.igteam.immersivegeology.ImmersiveGeology.MODID;

public class Helpers {
	
    public static ResourceLocation identifier(String name)
    {
        return new ResourceLocation(MODID, name);
    }
	
	 /**
     * Avoids IDE warnings by returning null for fields that are injected in by forge
     *
     * @return Not null!
     */
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> T getNull()
    {
        return null;
    }
}
