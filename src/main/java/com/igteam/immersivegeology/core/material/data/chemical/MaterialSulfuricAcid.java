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
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;


import net.minecraft.block.state.IBlockState;

import com.igteam.immersivegeology.core.material.data.enums.MetalEnum;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.types.MaterialChemical;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3d;
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
	public void entityInside(IBlockState state, World level, BlockPos pos, Entity entity)
	{
		if (entity instanceof EntityLivingBase living) {
			// Apply wither effect to players and zombies
			if (living instanceof EntityPlayer || living instanceof EntityZombie || living instanceof EntityAnimal) {
				living.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.WITHER, 60, 1));
			}

			// Additional logic for zombies
			if (living instanceof EntityZombie zombie && zombie.getHealth() < 4) {
				if (!level.isRemote) {
					if(!zombie.isChild()) {
						zombie.setDead();
					}
				} else {
					level.spawnParticle(net.minecraft.util.EnumParticleTypes.EXPLOSION_NORMAL,
							zombie.posX, zombie.posY, zombie.posZ,
							0, 0.0625, 0
					);
				}
			}
			if(!(living instanceof EntitySkeleton)) living.setFire(40);
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
