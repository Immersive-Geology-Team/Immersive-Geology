/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.helper;

import com.google.common.base.Suppliers;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum ToolTierHelper implements Tier
{
	UNOBTANIUM(99999, 2.0F, 0.0F, 15, () -> {
		return Ingredient.of(MetalEnum.Unobtanium.getItemTag(ItemCategoryFlags.INGOT));
	});

	private final int uses;
	private final float speed;
	private final float damage;
	private final int enchantmentValue;
	private final Supplier<Ingredient> repairIngredient;

	private ToolTierHelper(int pUses, float pSpeed, float pDamage, int pEnchantmentValue, Supplier<Ingredient> pRepairIngredient) {
		this.uses = pUses;
		this.speed = pSpeed;
		this.damage = pDamage;
		this.enchantmentValue = pEnchantmentValue;
		this.repairIngredient = Suppliers.memoize(pRepairIngredient::get);
	}

	public int getUses() {
		return this.uses;
	}

	public float getSpeed() {
		return this.speed;
	}

	public float getAttackDamageBonus() {
		return this.damage;
	}

	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	public @NotNull Ingredient getRepairIngredient() {
		return this.repairIngredient.get();
	}

	@Override
	public TagKey<Block> getIncorrectBlocksForDrops() {
		return Tiers.WOOD.getIncorrectBlocksForDrops();
	}
}
