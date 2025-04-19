/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.mixin.accessors;

import com.igteam.immersivegeology.common.menu.IGCrateMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinContainerMenu
{
	@Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
	private void overrideMaxStackSize(CallbackInfoReturnable<Integer> cir) {
//		if (IGCrateMenu.ACTIVE.get()) {
//			cir.setReturnValue(1024); // Any large enough value, or your own logic here
//		}
	}
}
