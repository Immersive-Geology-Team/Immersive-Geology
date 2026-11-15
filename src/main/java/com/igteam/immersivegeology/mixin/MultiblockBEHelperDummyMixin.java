/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.mixin;

import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperDummy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MultiblockBEHelperDummy.class, remap = false)
public class MultiblockBEHelperDummyMixin {
	@Inject(method = "load", at = @At("HEAD"))
	private void immersivegeology$convertLegacyPosition(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci) {
		String key = tag.contains("posInMultiblock", Tag.TAG_COMPOUND) ? "posInMultiblock": "posInMB";
		if (!tag.contains(key, Tag.TAG_COMPOUND)) return;
		CompoundTag position = tag.getCompound(key);
		if (!position.contains("X", Tag.TAG_ANY_NUMERIC)
				|| !position.contains("Y", Tag.TAG_ANY_NUMERIC)
				|| !position.contains("Z", Tag.TAG_ANY_NUMERIC)) return;
		tag.put("posInMB", NbtUtils.writeBlockPos(new BlockPos(
				position.getInt("X"), position.getInt("Y"), position.getInt("Z"))));
		if (key.equals("posInMultiblock")) tag.remove(key);
	}
}
