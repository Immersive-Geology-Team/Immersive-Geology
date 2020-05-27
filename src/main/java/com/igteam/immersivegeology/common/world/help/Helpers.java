package com.igteam.immersivegeology.common.world.help;

import javax.annotation.Nonnull;

public class Helpers {
	
	
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
