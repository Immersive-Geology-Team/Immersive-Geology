/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import blusunrize.immersiveengineering.api.tool.IDrillHead;
import blusunrize.immersiveengineering.common.items.DrillItem;
import blusunrize.immersiveengineering.common.items.DrillheadItem.DrillHeadPerm;
import blusunrize.immersiveengineering.common.register.IEItems.Tools;
import blusunrize.immersiveengineering.common.util.Utils;
import com.google.common.collect.ImmutableList;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class IGGenericDrillHead extends IGGenericItem implements IDrillHead
{
	public DrillHeadProps perms;
	public IGGenericDrillHead(ItemCategoryFlags flag, MaterialInterface<?> material)
	{
		super(flag, material, new Properties().stacksTo(1).durability(material.drillHeadInstance().maxDamage));
		this.perms = material.drillHeadInstance();
	}

	@Override
	public boolean isEnchantable(ItemStack pStack)
	{
		return false;
	}

	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
		list.add(Component.translatable("desc.immersiveengineering.flavour.drillhead.size", new Object[]{this.perms.drillSize, this.perms.drillDepth}));
		list.add(Component.translatable("desc.immersiveengineering.flavour.drillhead.level", new Object[]{Utils.getHarvestLevelName(this.getMiningLevel(stack))}));
		list.add(Component.translatable("desc.immersiveengineering.flavour.drillhead.speed", new Object[]{Utils.formatDouble((double)this.getMiningSpeed(stack), "0.###")}));
		list.add(Component.translatable("desc.immersiveengineering.flavour.drillhead.damage", new Object[]{Utils.formatDouble((double)this.getAttackDamage(stack), "0.###")}));
		int maxDmg = this.getMaximumHeadDamage(stack);
		int dmg = maxDmg - this.getHeadDamage(stack);
		float quote = (float)dmg / (float)maxDmg;
		ChatFormatting var10000 = (double)quote < 0.1 ? ChatFormatting.RED : ((double)quote < 0.3 ? ChatFormatting.GOLD : ((double)quote < 0.6 ? ChatFormatting.YELLOW : ChatFormatting.GREEN));
		String status = "" + var10000;
		String s = status + (this.getMaximumHeadDamage(stack) - this.getHeadDamage(stack)) + "/" + this.getMaximumHeadDamage(stack);
		list.add(Component.translatable("desc.immersiveengineering.info.durability", new Object[]{s}));
	}

	public boolean isValidRepairItem(ItemStack stack, ItemStack material) {
		return material.is(this.perms.repairMaterial);
	}

	public boolean beforeBlockbreak(ItemStack drill, ItemStack head, Player player) {
		return false;
	}

	public void afterBlockbreak(ItemStack drill, ItemStack head, Player player) {
	}

	public Tier getMiningLevel(ItemStack head) {
		return this.perms.drillLevel;
	}

	public float getMiningSpeed(ItemStack head) {
		return this.perms.drillSpeed;
	}

	public float getAttackDamage(ItemStack head) {
		return this.perms.drillAttack;
	}

	public int getHeadDamage(ItemStack head) {
		if (head.hasTag()) {
			CompoundTag nbt = head.getOrCreateTag();
			return nbt.contains("headDamage", 3) ? nbt.getInt("headDamage") : nbt.getInt("Damage");
		} else {
			return 0;
		}
	}

	public int getMaximumHeadDamage(ItemStack head) {
		return this.perms.maxDamage;
	}

	public void damageHead(ItemStack head, int dmg) {
		setHeadDamage(head, this.getHeadDamage(head) + dmg);
	}

	public static void setHeadDamage(ItemStack head, int totalDamage) {
		CompoundTag nbt = head.getOrCreateTag();
		nbt.remove("headDamage");
		nbt.putInt("Damage", totalDamage);
	}

	public ResourceLocation getDrillTexture(ItemStack drill, ItemStack head) {
		return this.perms.texture;
	}

	public int getBarWidth(@Nonnull ItemStack stack) {
		return Math.round(13.0F * (1.0F - (float)this.getHeadDamage(stack) / (float)this.getMaximumHeadDamage(stack)));
	}

	public boolean isBarVisible(@Nonnull ItemStack stack) {
		return this.getHeadDamage(stack) > 0;
	}

	public ImmutableList<BlockPos> getExtraBlocksDug(ItemStack head, Level world, Player player, HitResult rtr) {
		if (!(rtr instanceof BlockHitResult brtr)) {
			return ImmutableList.of();
		} else {
			Direction side = brtr.getDirection();
			int diameter = this.perms.drillSize;
			int depth = this.perms.drillDepth;
			BlockPos startPos = brtr.getBlockPos();
			BlockState state = world.getBlockState(startPos);
			float maxHardness = 1.0F;
			if (!state.isAir()) {
				maxHardness = state.getDestroyProgress(player, world, startPos) * 0.4F;
			}

			if (maxHardness < 0.0F) {
				maxHardness = 0.0F;
			}

			if (diameter % 2 == 0) {
				float hx = (float)brtr.getLocation().x - (float)brtr.getBlockPos().getX();
				float hy = (float)brtr.getLocation().y - (float)brtr.getBlockPos().getY();
				float hz = (float)brtr.getLocation().z - (float)brtr.getBlockPos().getZ();
				if (side.getAxis() == Axis.Y && (double)hx < 0.5 || side.getAxis() == Axis.Z && (double)hx < 0.5) {
					startPos = startPos.offset(-diameter / 2, 0, 0);
				}

				if (side.getAxis() != Axis.Y && (double)hy < 0.5) {
					startPos = startPos.offset(0, -diameter / 2, 0);
				}

				if (side.getAxis() == Axis.Y && (double)hz < 0.5 || side.getAxis() == Axis.X && (double)hz < 0.5) {
					startPos = startPos.offset(0, 0, -diameter / 2);
				}
			} else {
				startPos = startPos.offset(-(side.getAxis() == Axis.X ? 0 : diameter / 2), -(side.getAxis() == Axis.Y ? 0 : diameter / 2), -(side.getAxis() == Axis.Z ? 0 : diameter / 2));
			}

			ImmutableList.Builder<BlockPos> b = ImmutableList.builder();

			for(int dd = 0; dd < depth; ++dd) {
				for(int dw = 0; dw < diameter; ++dw) {
					for(int dh = 0; dh < diameter; ++dh) {
						BlockPos pos = startPos.offset(side.getAxis() == Axis.X ? dd : dw, side.getAxis() == Axis.Y ? dd : dh, side.getAxis() == Axis.Y ? dh : (side.getAxis() == Axis.X ? dw : dd));
						if (!pos.equals(brtr.getBlockPos())) {
							state = world.getBlockState(pos);
							if (!state.isAir()) {
								Block block = state.getBlock();
								float h = state.getDestroyProgress(player, world, pos);
								boolean canHarvest = block.canHarvestBlock(world.getBlockState(pos), world, pos, player);
								boolean drillMat = ((DrillItem)Tools.DRILL.get()).isEffective(ItemStack.EMPTY, state);
								boolean hardness = h >= maxHardness;
								if (canHarvest && drillMat && hardness) {
									b.add(pos);
								}
							}
						}
					}
				}
			}

			return b.build();
		}
	}
	public static class DrillHeadProps {
		final String name;
		final TagKey<Item> repairMaterial;
		final int drillSize;
		final int drillDepth;
		final Tier drillLevel;
		final float drillSpeed;
		final float drillAttack;
		final int maxDamage;
		public final ResourceLocation texture;

		public DrillHeadProps(String name, TagKey<Item> repairMaterial, int drillSize, int drillDepth, Tier drillLevel, float drillSpeed, int drillAttack, int maxDamage, ResourceLocation texture) {
			this.name = name;
			this.repairMaterial = repairMaterial;
			this.drillSize = drillSize;
			this.drillDepth = drillDepth;
			this.drillLevel = drillLevel;
			this.drillSpeed = drillSpeed;
			this.drillAttack = (float)drillAttack;
			this.maxDamage = maxDamage;
			this.texture = texture;
		}
	}
}
