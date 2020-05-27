package com.igteam.immersivegeology.common.blocks.property;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;

public class IGProperties {
	public static final BooleanProperty NATURAL = BooleanProperty.create("natural");
	public static final IntegerProperty HARDNESS = IntegerProperty.create("hardness", 0, 256);
	
	
}
