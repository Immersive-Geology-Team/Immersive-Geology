package com.igteam.immersivegeology.common.item.blueprint;

import com.igteam.immersivegeology.common.item.IGGenericItem;
import com.igteam.immersivegeology.common.item.blueprint.IGBlueprintSettings.Mode;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
			tooltip.add(Component.translatable("desc.immersivegeology.info.blueprint.build0", Component.literal("["+ size.getX() +"x" + size.getY() +"x" + size.getZ()+ "]")));
			tooltip.add(Component.translatable("desc.immersivegeology.info.blueprint.build1", Component.keybind("shift").withStyle(ChatFormatting.GOLD)));
		} else{
			tooltip.add(Component.translatable("desc.immersivegeology.info.blueprint.noMultiblock"));
		}
	}

	public static IGBlueprintSettings getSettings(@Nullable ItemStack stack){
		return new IGBlueprintSettings(stack);
	}

	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();

		if(player == null) return InteractionResult.SUCCESS;
		if(!hand.equals(InteractionHand.MAIN_HAND))
		{
			if(player.isShiftKeyDown())
			{
				ItemStack stack = player.getOffhandItem();
				IGBlueprintSettings settings = getSettings(stack);
				Rotation rot = player.getDirection().equals(Direction.NORTH) ? Rotation.NONE : (player.getDirection().equals(Direction.EAST) ? Rotation.CLOCKWISE_90 : (player.getDirection().equals(Direction.WEST) ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_180));
				settings.setRotation(rot);
				settings.applyTo(stack);
				settings.sendPacketToServer(hand);
				player.displayClientMessage(settings.getMode().getTranslated(), true);
			}
			return InteractionResult.SUCCESS;
		}
		BlockPos pos = context.getClickedPos().above();
		ItemStack stack = player.getMainHandItem();
		IGBlueprintSettings settings = getSettings(stack);
		settings.setPos(pos);
		settings.setPlaced(true);
		settings.applyTo(stack);
		settings.sendPacketToServer(hand);
		player.displayClientMessage(settings.getMode().getTranslated(), true);

		return InteractionResult.SUCCESS;
	}
}
