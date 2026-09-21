/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.core.material.data.chemical;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


import net.minecraft.block.state.IBlockState;

import com.igteam.immersivegeology.core.lib.shim.IGMultiblockRefs.IGChemicalReactorSkins;
import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;

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
	public void entityInside(IBlockState state, World level, BlockPos pos, Entity entity)
	{
		if (entity instanceof EntityLivingBase living) {
			// Apply wither effect to players and zombies
			if (living instanceof EntityPlayer|| living instanceof EntitySkeleton || living instanceof EntityWitch || living instanceof EntityAnimal) {
				living.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.WITHER, 60, 1));
			}

			// Additional logic for skeleton
			if (living instanceof EntitySkeleton skeleton && skeleton.getHealth() < 4) {
				if (!level.isRemote) {
					level.spawnEntity(new EntityItem(level, skeleton.posX,skeleton.posY,skeleton.posZ, new ItemStack(MineralEnum.Apatite.getItem(ItemCategoryFlags.POWDER))));
				} else {
					level.spawnParticle(net.minecraft.util.EnumParticleTypes.EXPLOSION_NORMAL,
							skeleton.posX, skeleton.posY, skeleton.posZ,
							0, 0.0625, 0
					);
				}
				skeleton.setDead();
			}

			//Flix Easter Egg
			if (living instanceof EntityWitch witch && witch.getHealth() < 4) {
				if (!level.isRemote) {
					level.spawnEntity(new EntityItem(level, witch.posX,witch.posY,witch.posZ, new ItemStack(IGChemicalReactorSkins.HAZARD.getItem())));
				} else {
					level.spawnParticle(net.minecraft.util.EnumParticleTypes.EXPLOSION_NORMAL,
							witch.posX, witch.posY, witch.posZ,
							0, 0.0625, 0
					);
				}
				witch.setDead();
			}

		}

		// Logic for items
		if (entity instanceof EntityItem item && !item.isImmuneToFire()) {
				level.spawnParticle(net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL,
						item.posX, item.posY, item.posZ,
						0, 0.0625, 0
				);

			if (rand.nextInt(60) == 0) {
				item.setFire(3);
			}
		}
	}
}
