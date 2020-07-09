package com.igteam.immersivegeology.common.blocks.property;

import com.igteam.immersivegeology.common.materials.EnumOreBearingMaterials;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;

public class IGProperties {
	public static final BooleanProperty NATURAL = BooleanProperty.create("natural");
	public static final IntegerProperty HARDNESS = IntegerProperty.create("hardness", 0, 128);
	public static final IntegerProperty ORE_RICHNESS = IntegerProperty.create("richness", 0, 3);
}
