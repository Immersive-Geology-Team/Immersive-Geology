/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.chemical;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.HashMap;
import java.util.Random;

public class MaterialSulfuricAcid extends MaterialChemical
{
	public MaterialSulfuricAcid()
	{
		super(MetalEnum.Zinc, MetalEnum.Chromium, MetalEnum.Nickel, MetalEnum.Neodymium,
				MetalEnum.Iron, MineralEnum.Pyrite, MineralEnum.Millerite,
				MineralEnum.Vanadinite);
	}
	Random rand = new Random();
	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
	{
		if (entity instanceof LivingEntity living) {
			// Apply wither effect to players and zombies
			if (living instanceof Player || living instanceof Zombie || living instanceof Animal) {
				living.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
			}

			// Additional logic for zombies
			if (living instanceof Zombie zombie && zombie.getHealth() < 4) {
				if (level instanceof ServerLevel) {
					if(!zombie.isBaby()) {
						zombie.convertTo(EntityType.SKELETON, true);
					}
				} else if (level instanceof ClientLevel client) {
					client.addParticle(
							ParticleTypes.POOF,
							zombie.getX(), zombie.getY(), zombie.getZ(),
							0, 0.0625, 0
					);
				}
			}
		}

		// Logic for items
		if (entity instanceof ItemEntity item && !item.fireImmune()) {
			if (level instanceof ClientLevel client) {
				client.addParticle(
						ParticleTypes.SMOKE,
						item.getX(), item.getY(), item.getZ(),
						0, 0.0625, 0
				);
			}
			if (rand.nextInt(60) == 0) {
				item.setSecondsOnFire(3);
			}
		}
	}
}
