package com.igteam.immersivegeology.common.item.blueprint;

import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler.IMultiblock;
import com.igteam.immersivegeology.common.block.multiblocks.IGTemplateMultiblock;
import com.igteam.immersivegeology.common.item.IGGenericItem;
import com.igteam.immersivegeology.common.item.blueprint.BlueprintProjection.Info;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;

public class IGMultiblockBlueprint extends IGGenericItem
{
	public IGMultiblockBlueprint(ItemCategoryFlags flag, MaterialInterface<?> material)
	{
		super(flag, material, new Properties().stacksTo(1));
	}

	@Override
	public @NotNull Component getName(ItemStack stack)
	{
		String selfKey = getDescriptionId(stack);
		if(stack.hasTag())
		{
			IGBlueprintSettings settings = getSettings(stack);
			if(settings.getMultiblock() != null)
			{
				Component name = settings.getMultiblock().getDisplayName();
				return Component.translatable(selfKey+".specific", name).withStyle(ChatFormatting.AQUA);
			}
		}
		return Component.translatable(selfKey).withStyle(ChatFormatting.AQUA);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag pIsAdvanced)
	{
		IGBlueprintSettings settings = getSettings(stack);
		if(settings.getMultiblock() != null){
			assert worldIn!=null;
			Vec3i size = settings.getMultiblock().getSize(worldIn);
			tooltip.add(Component.translatable("desc.immersivegeology.info.blueprint.size", Component.literal("["+ size.getX() +"x" + size.getY() +"x" + size.getZ()+ "]")));
			IMultiblock mb = settings.getMultiblock();
			String machine_tier_id = "desc.immersivegeology.info.blueprint.ie_tier";
			if(mb instanceof IGTemplateMultiblock igTemplate)
			{
				machine_tier_id = "desc.immersivegeology.info.blueprint.ig_tier_" + igTemplate.getFormationTier();
			}

			tooltip.add(Component.translatable("desc.immersivegeology.info.blueprint.tier", Component.translatable(machine_tier_id).withStyle(ChatFormatting.AQUA)));

			tooltip.add(Component.translatable("desc.immersivegeology.info.blueprint.block_info", Component.keybind("shift").withStyle(ChatFormatting.GOLD)));
		} else{
			tooltip.add(Component.translatable("desc.immersivegeology.info.blueprint.no_multiblock"));
		}
	}

	public static IGBlueprintSettings getSettings(@Nullable ItemStack stack){
		return new IGBlueprintSettings(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Player player = context.getPlayer();
		if(player == null) return InteractionResult.SUCCESS;
		Level level = context.getLevel();
		InteractionHand hand = context.getHand();
		Direction facing = context.getClickedFace();
		BlockPos pos = context.getClickedPos().above();
		ItemStack stack = player.getItemInHand(hand);
		IGBlueprintSettings settings = getSettings(stack);

		if(!hand.equals(InteractionHand.MAIN_HAND))
		{
			if(player.isShiftKeyDown())
			{
				Rotation rot = player.getDirection().equals(Direction.NORTH) ? Rotation.NONE : (player.getDirection().equals(Direction.EAST) ? Rotation.CLOCKWISE_90 : (player.getDirection().equals(Direction.WEST) ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_180));
				settings.setRotation(rot);
				settings.applyTo(stack);
				player.displayClientMessage(Component.translatable("desc.immersivegeology.info.blueprint.rotated"), true);

				return InteractionResult.SUCCESS;
			}
		}
		settings.setPos(pos);
		settings.setPlaced(true);
		settings.applyTo(stack);

		player.displayClientMessage(Component.translatable("desc.immersivegeology.info.blueprint.moved"), true);

		return InteractionResult.SUCCESS;
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		IGBlueprintSettings settings = getSettings(stack);
		BlockPos pos = settings.getPos();
		if(player.isShiftKeyDown() && player.isCreative() && pos != null && settings.getMultiblock() != null)
		{
			final MutableBlockPos hit = pos.mutable();
			if(!level.isClientSide)
			{
				// Creative Placement
				BiPredicate<Integer, Info> pred = (layer, info) -> {
					BlockPos realPos = info.tPos.offset(hit);
					BlockState to_state = info.getModifiedState(level, realPos);
					level.setBlockAndUpdate(realPos, to_state);
					return false; // Don't ever skip a step.
				};

				BlueprintProjection projection = new BlueprintProjection(level, settings.getMultiblock());
				projection.setFlip(settings.isMirrored());
				projection.setRotation(settings.getRotation());
				projection.processAll(pred);

				player.displayClientMessage(Component.translatable("desc.immersivegeology.info.blueprint.placed"), true);

				return InteractionResultHolder.success(stack);
			}
		}
		return InteractionResultHolder.pass(stack);
	}
}
