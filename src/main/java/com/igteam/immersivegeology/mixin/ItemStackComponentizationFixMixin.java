/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.mixin;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemStackComponentizationFix.class)
public class ItemStackComponentizationFixMixin {
	@Inject(method = "fixItemStack", at = @At("TAIL"))
	private static void immersivegeology$translateMapNamesIntoComponents(ItemStackComponentizationFix.ItemStackData itemStackData, Dynamic<?> tag, CallbackInfo ci) {
		if (itemStackData.is("immersivegeology:metal_detector") && itemStackData.hasComponent("minecraft:block_entity_data")) {
			tag.get("BlockEntityTag").result().ifPresent(blockEntityTag -> {
				if (blockEntityTag.get("id").result().isEmpty())
					itemStackData.setComponent("minecraft:block_entity_data", blockEntityTag.set("id", blockEntityTag.createString("immersivegeology:metal_detector_type")));
			});
		}
		if (!itemStackData.is("minecraft:filled_map")) return;
		List<? extends Dynamic<?>> mapMarks = tag.get("Decorations").asStream()
				.filter(entry ->
						entry.get("id").asString("").startsWith("ig_deposit_") && entry.get("IGName").asString().result().isPresent())
				.map(entry ->
						entry.emptyMap()
								.set("id", entry.createString(entry.get("id").asString("")))
								.set("IGName", entry.createString(entry.get("IGName").asString(""))))
				.toList();
		if (!mapMarks.isEmpty())
			itemStackData.fixSubTag("Decorations", false, subTag -> subTag.createList(mapMarks.stream()));
	}
}
