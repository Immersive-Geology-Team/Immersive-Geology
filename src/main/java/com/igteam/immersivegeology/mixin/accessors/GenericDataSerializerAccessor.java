/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.mixin.accessors;

import blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers;
import blusunrize.immersiveengineering.common.gui.sync.GenericDataSerializers.DataSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(GenericDataSerializers.class)
public interface GenericDataSerializerAccessor
{
	@Accessor("SERIALIZERS")
	static List<DataSerializer<?>> getSerializers() {
		throw new AssertionError();
	}
}
