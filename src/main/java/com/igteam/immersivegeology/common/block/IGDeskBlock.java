/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block;

import blusunrize.immersiveengineering.common.blocks.wooden.DeskBlock;
import blusunrize.immersiveengineering.common.gui.IEBaseContainerOld;
import com.igteam.immersivegeology.client.menu.ItemSubGroup;
import com.igteam.immersivegeology.common.block.helper.IGBlockType;
import com.igteam.immersivegeology.common.block.helper.IInteractionObjectIG;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.flags.IFlagType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class IGDeskBlock<T extends BlockEntity> extends DeskBlock<T> implements IGBlockType
{
	public IGDeskBlock(RegistryObject tile, Properties props)
	{
		super(tile, props);
	}

	@Override
	public Block getIGBlock()
	{
		return this;
	}

	@Override
	public @NotNull Collection<MaterialInterface<?>> getMaterials()
	{
		return List.of();
	}

	@Override
	public MaterialInterface<?> getMaterial(MaterialTexture t)
	{
		return null;
	}

	@Override
	public IFlagType<?> getFlag()
	{
		return BlockCategoryFlags.MISC;
	}

	@Override
	public ItemSubGroup getGroup()
	{
		return ItemSubGroup.structural;
	}

	@Override
	public Map<MaterialTexture, MaterialInterface<?>> getMaterialMap()
	{
		return Map.of();
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		BlockEntity tile = world.getBlockEntity(pos);
		InteractionResult superResult = InteractionResult.SUCCESS;
		if(tile instanceof MenuProvider menuProvider&&hand==InteractionHand.MAIN_HAND&&!player.isShiftKeyDown())
		{
			if(player instanceof ServerPlayer serverPlayer)
			{
				if(menuProvider instanceof IInteractionObjectIG<?> interaction)
				{
					interaction = interaction.getGuiMaster();
					if(interaction!=null&&interaction.canUseGui(player))
					{
						// This can be removed once IEBaseContainerOld is gone
						var tempMenu = interaction.createMenu(0, player.getInventory(), player);
						if(tempMenu instanceof IEBaseContainerOld<?>)
							NetworkHooks.openScreen(serverPlayer, interaction, ((BlockEntity)interaction).getBlockPos());
						else
							NetworkHooks.openScreen(serverPlayer, interaction);
					}
				}
				else
					NetworkHooks.openScreen(serverPlayer, menuProvider);
			}
			return InteractionResult.SUCCESS;
		}
		return superResult;
	}

	@Override
	public int getColor(int index, BlockState state)
	{
		return 0xffffff;
	}
}
