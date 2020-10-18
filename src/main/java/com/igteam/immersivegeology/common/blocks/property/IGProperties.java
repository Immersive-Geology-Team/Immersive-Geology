package com.igteam.immersivegeology.common.blocks.property;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

public class IGProperties
{
	public static final BooleanProperty NATURAL = BooleanProperty.create("natural");
	public static final IntegerProperty ORE_RICHNESS = IntegerProperty.create("richness", 0, 3);
	public static final EnumProperty<SpikePart> PART;


	static{
		PART = EnumProperty.create("part", SpikePart.class);
	}

}