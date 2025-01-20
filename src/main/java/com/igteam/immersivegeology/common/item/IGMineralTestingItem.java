/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.world.IGOreFeature;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.MineralEnum;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialHelper;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Biomes;

import java.util.*;

public class IGMineralTestingItem extends IGGenericItem
{
	public IGMineralTestingItem(ItemCategoryFlags flag, MaterialInterface<?> material)
	{
		super(flag, material);
	}

	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		BlockPos usedPos = context.getClickedPos();
		Holder<Biome> biome = level.getBiome(usedPos);

		Map<MaterialInterface<?>, Integer> queryMap = new HashMap<>();
		ChunkAccess centreChunk = level.getChunk(usedPos);
		ChunkPos usedChunk = centreChunk.getPos();
		BlockPos chunkWorldPosition = centreChunk.getPos().getWorldPosition();

		StoneEnum stone = StoneEnum.selectWorldState(level.getBlockState(usedPos));
		int height = centreChunk.getHeight();
		if(stone != null) level.setBlock(usedPos, MineralEnum.Acanthite.getOreBlock(stone, OreRichness.RICH).getDefaultBlockState(), 3);
		//IGLib.IG_LOGGER.info(queryMap.toString());

//		for(int x = -64; x < (16+64); ++x)
//		{
//			for(int z = -64; z < (16+64); ++z)
//			{
//				for(int y = -60; y < height; ++y)
//				{
//					BlockPos cursor = new BlockPos(chunkWorldPosition).offset(x, y, z);
//					BlockState check = level.getBlockState(cursor);
//					if(check.getBlock() instanceof IOreBlock ore)
//					{
//						MaterialInterface<?> material = ore.getMaterial(MaterialTexture.overlay);
//						int amount = queryMap.getOrDefault(material, 1);
//						queryMap.put(material, (amount+1));
//					} else {
//						StoneEnum stone = StoneEnum.selectWorldState(check);
//
//					}
//				}
//			}
//		}

		return InteractionResult.SUCCESS;
	}
}
