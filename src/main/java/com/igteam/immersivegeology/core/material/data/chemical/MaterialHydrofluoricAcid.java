/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.chemical;

import com.igteam.immersivegeology.common.block.multiblocks.skins.IGChemicalReactorSkins;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.security.spec.RSAPrivateKeySpec;
import java.util.Random;

public class MaterialHydrofluoricAcid extends MaterialChemical
{

	public MaterialHydrofluoricAcid()
	{
		super(MetalEnum.Neodymium, MetalEnum.Uranium, MetalEnum.Thorium, MetalEnum.Neodymium);
	}

	Random rand = new Random();
	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
	{
		if (entity instanceof LivingEntity living) {
			// Apply wither effect to players and zombies
			if (living instanceof Player|| living instanceof Skeleton || living instanceof Witch || living instanceof Animal) {
				living.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
			}

			// Additional logic for skeleton
			if (living instanceof Skeleton skeleton && skeleton.getHealth() < 4) {
				if (level instanceof ServerLevel serverLevel) {
					serverLevel.addFreshEntity(new ItemEntity(serverLevel, skeleton.getX(),skeleton.getY(),skeleton.getZ(), new ItemStack(MineralEnum.Apatite.getItem(ItemCategoryFlags.POWDER))));
				} else if (level instanceof ClientLevel client) {
					client.addParticle(
							ParticleTypes.POOF,
							skeleton.getX(), skeleton.getY(), skeleton.getZ(),
							0, 0.0625, 0
					);
				}
				skeleton.remove(RemovalReason.DISCARDED);
			}

			//Flix Easter Egg
			if (living instanceof Witch witch && witch.getHealth() < 4) {
				if (level instanceof ServerLevel serverLevel) {
					serverLevel.addFreshEntity(new ItemEntity(serverLevel, witch.getX(),witch.getY(),witch.getZ(), new ItemStack(IGChemicalReactorSkins.HAZARD.getItem())));
				} else if (level instanceof ClientLevel client) {
					client.addParticle(
							ParticleTypes.POOF,
							witch.getX(), witch.getY(), witch.getZ(),
							0, 0.0625, 0
					);
				}
				witch.remove(RemovalReason.DISCARDED);
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
