/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.multiblocks.skins.helpers;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public interface IMultiSkinBlock<T extends Enum<T> & IIGMultiSkinHelper & StringRepresentable> {
	EnumProperty<T> getSkinProperty();

	Class<T> getSkinClass();
}

