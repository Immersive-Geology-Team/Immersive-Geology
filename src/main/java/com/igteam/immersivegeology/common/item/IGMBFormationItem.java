/*
 * Muddykat
 * Copyright (c) 2024
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.item;

import blusunrize.immersiveengineering.api.multiblocks.BlockMatcher;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockAdvancementTrigger;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.api.utils.ItemUtils;
import com.google.common.collect.ImmutableList;
import com.igteam.immersivegeology.common.block.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.IStoneType;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.neoforged.neoforge.common.Tags.Items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiPredicate;

public class IGMBFormationItem extends IGGenericItem
{
	BiPredicate<IMultiblock, MaterialInterface<?>> validPredicate = (multiblock, material) -> material.canFormMB(multiblock);
	public IGMBFormationItem(ItemCategoryFlags flag, MaterialInterface<?> material, int max_durability)
	{
		super(flag, material, new Properties().durability(max_durability));
	}

	@Override
	public int getMaxStackSize(ItemStack stack)
	{
		return 1;
	}

	@Override
	public Component getName(ItemStack pStack) {
		return Component.translatable(this.getDescriptionId(pStack));
	}

	@Override
	public int getColor(int index)
	{
		return 0xffffff;
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		Direction side = context.getClickedFace();
		List<ResourceLocation> permittedMultiblocks = null;
		List<ResourceLocation> interdictedMultiblocks = null;
		CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		if(customData.contains("multiblockPermission"))
		{
			ListTag list = customData.copyTag().getList("multiblockPermission", Tag.TAG_STRING);
			permittedMultiblocks = parseMultiblockNames(list, player, "permission");
			if(permittedMultiblocks==null)
				return InteractionResult.FAIL;
		}
		if(customData.contains("multiblockInterdiction"))
		{
			ListTag list = customData.copyTag().getList("multiblockInterdiction", Tag.TAG_STRING);
			interdictedMultiblocks = parseMultiblockNames(list, player, "interdiction");
			if(interdictedMultiblocks==null)
				return InteractionResult.FAIL;
		}
		final Direction multiblockSide;
		if(side.getAxis()==Axis.Y&&player!=null)
			multiblockSide = Direction.fromYRot(player.getYRot()).getOpposite();
		else
			multiblockSide = side;
		for(MultiblockHandler.IMultiblock mb : MultiblockHandler.getMultiblocks())
		{
			boolean isValid = validPredicate.test(mb,getMaterial(MaterialTexture.base));
			boolean isBlockTrigger = mb.isBlockTrigger(world.getBlockState(pos), multiblockSide, world);

			if (isBlockTrigger)
			{
				boolean isAllowed;
				if(permittedMultiblocks!=null)
					isAllowed = permittedMultiblocks.contains(mb.getUniqueName());
				else if(interdictedMultiblocks!=null)
					isAllowed = !interdictedMultiblocks.contains(mb.getUniqueName());
				else
					isAllowed = true;
				if(!isAllowed)
					continue;
				if(MultiblockHandler.postMultiblockFormationEvent(player, mb, pos, stack).isCanceled())
					continue;
				if(isValid)
				{
					if(mb.createStructure(world, pos, multiblockSide, player))
					{
						if(player instanceof ServerPlayer sPlayer)
							MultiblockAdvancementTrigger.INSTANCE.get().trigger(sPlayer, mb, stack);


						if(player!=null)
							stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
						return InteractionResult.SUCCESS;
					}
				} else if(player!=null)
				{
					if(confirmMBStructure((TemplateMultiblock) mb, world, pos, side, player)) player.displayClientMessage(Component.translatable("immersivegeology.multiblock.formation.failed"), true);
				}
			}
		}

		return InteractionResult.PASS;
	}

	public static boolean confirmMBStructure(TemplateMultiblock mb, Level world, BlockPos pos, Direction side, Player player) {
		Rotation rot = DirectionUtils.getRotationBetweenFacings(Direction.NORTH, side.getOpposite());
		if (rot == null) {
			return false;
		} else {
			List<StructureBlockInfo> structure = mb.getStructure(world);
			List<Mirror> mirror_states = mb.canBeMirrored() ? ImmutableList.of(Mirror.NONE, Mirror.FRONT_BACK) : ImmutableList.of(Mirror.NONE);
			Iterator<Mirror> var7 = mirror_states.iterator();

			label29:
			while(var7.hasNext()) {
				Mirror mirror = (Mirror)var7.next();
				StructurePlaceSettings placeSet = (new StructurePlaceSettings()).setMirror(mirror).setRotation(rot);
				BlockPos origin = pos.subtract(StructureTemplate.calculateRelativePosition(placeSet, mb.getTriggerOffset()));
				Iterator var11 = structure.iterator();

				while(var11.hasNext()) {
					StructureTemplate.StructureBlockInfo info = (StructureTemplate.StructureBlockInfo)var11.next();
					BlockPos realRelPos = StructureTemplate.calculateRelativePosition(placeSet, info.pos());
					BlockPos here = origin.offset(realRelPos);
					BlockState expected = info.state().mirror(mirror).rotate(rot);
					BlockState inWorld = world.getBlockState(here);
					if (!BlockMatcher.matches(expected, inWorld, world, here).isAllow()) {
						continue label29;
					}
				}

				return true;
			}

			return false;
		}
	}

	@Nullable
	private static List<ResourceLocation> parseMultiblockNames(ListTag data, @Nullable Player player, String prefix)
	{
		List<ResourceLocation> result = new ArrayList<>();
		for(int i = 0; i < data.size(); ++i)
		{
			String listEntry = data.getString(i);
			ResourceLocation asRL = ResourceLocation.tryParse(listEntry);
			if(asRL==null||MultiblockHandler.getByUniqueName(asRL)==null)
			{
				if(player!=null&&!player.getCommandSenderWorld().isClientSide)
					player.displayClientMessage(Component.literal("Invalid "+prefix+" entry: "+listEntry), false);
				return null;
			}
			result.add(asRL);
		}
		return result;
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack)
	{
		return true;
	}
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
		return true;
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return enchantment.is(Enchantments.UNBREAKING) || enchantment.is(Enchantments.MENDING);
	}

	@Override
	public boolean isIGRepairable(ItemStack stack)
	{
		return true;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingRemainingItem(@Nonnull ItemStack stack) {
		return ItemUtils.damageCopy(stack, 1);
	}

	public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
		if(materialMap.get(MaterialTexture.base) instanceof IStoneType) return repairCandidate.is(Items.COBBLESTONES);
		return repairCandidate.is(materialMap.get(MaterialTexture.base).getItem(ItemCategoryFlags.INGOT));
	}

	public boolean canFormMB(MaterialInterface<?> material, IGTemplateMultiblock igTemplateMultiblock)
	{
		return validPredicate.test(igTemplateMultiblock, material);
	}
}
