/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.entity;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.client.IModelOffsetProvider;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHasDummyBlocks;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IInteractionObjectIE;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IStateBasedDirectional;
import blusunrize.immersiveengineering.common.blocks.PlacementLimitation;
import blusunrize.immersiveengineering.common.blocks.wooden.DeskBlock;
import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import com.google.common.base.Preconditions;
import com.igteam.immersivegeology.common.block.helper.IInteractionObjectIG;
import com.igteam.immersivegeology.core.registration.IGMenuTypes;
import com.igteam.immersivegeology.core.registration.IGMenuTypes.ArgContainer;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DrawingTableBlockEntity extends IEBaseBlockEntity implements IIEInventory, IStateBasedDirectional,
		IHasDummyBlocks, IModelOffsetProvider, IInteractionObjectIG<DrawingTableBlockEntity>
{
	public static final BlockPos MASTER_POS = BlockPos.ZERO;
	public static final BlockPos DUMMY_POS = new BlockPos(1, 0, 0);
	private final NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

	public DrawingTableBlockEntity(BlockPos pos, BlockState state)
	{
		super(IGRegistrationHolder.DRAWING_TABLE.get(), pos, state);
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean descPacket)
	{
		ContainerHelper.loadAllItems(nbt, inventory);
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean descPacket)
	{
		ContainerHelper.saveAllItems(nbt, inventory);
	}

	private AABB renderAABB;

	@Override
	public AABB getRenderBoundingBox()
	{
		if(renderAABB==null)
			renderAABB = new AABB(getBlockPos().getX()-1, getBlockPos().getY(), getBlockPos().getZ()-1, getBlockPos().getX()+2, getBlockPos().getY()+2, getBlockPos().getZ()+2);
		return renderAABB;
	}

	@Override
	public NonNullList<ItemStack> getInventory()
	{
		return this.inventory;
	}

	@Override
	public boolean isStackValid(int slot, ItemStack stack)
	{
		return true;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return slot==0?1: 64;
	}

	@Override
	public void doGraphicalUpdates() {}

	@Override
	public @NotNull PlacementLimitation getFacingLimitation()
	{
		return PlacementLimitation.HORIZONTAL;
	}

	@Override
	public boolean canHammerRotate(Direction side, Vec3 hit, LivingEntity entity)
	{
		return false;
	}

	@Override
	public boolean isDummy()
	{
		return getState().getValue(IEProperties.MULTIBLOCKSLAVE);
	}

	@Nullable
	@Override
	public DrawingTableBlockEntity master()
	{
		if(!isDummy())
			return this;
		// Used to provide tile-dependant drops after breaking
		if(tempMasterBE!=null)
			return (DrawingTableBlockEntity)tempMasterBE;
		Direction dummyDir = isDummy()?getFacing().getCounterClockWise(): getFacing().getClockWise();
		BlockPos masterPos = getBlockPos().relative(dummyDir);
		BlockEntity te = Utils.getExistingTileEntity(level, masterPos);
		return (te instanceof DrawingTableBlockEntity)?(DrawingTableBlockEntity)te: null;
	}

	@Override
	public void placeDummies(BlockPlaceContext ctx, BlockState state)
	{
		DeskBlock.placeDummies(getBlockState(), level, worldPosition, ctx);
	}

	@Override
	public void breakDummies(BlockPos pos, BlockState state)
	{
		tempMasterBE = master();
		Direction dummyDir = isDummy()?getFacing().getCounterClockWise(): getFacing().getClockWise();
		level.removeBlock(pos.relative(dummyDir), false);
	}

	@Override
	public DrawingTableBlockEntity getGuiMaster()
	{
		if(!isDummy())
			return this;
		Direction dummyDir = getFacing().getCounterClockWise();
		BlockEntity tileEntityModWorkbench = level.getBlockEntity(worldPosition.relative(dummyDir));
		if(tileEntityModWorkbench instanceof DrawingTableBlockEntity)
			return (DrawingTableBlockEntity)tileEntityModWorkbench;
		return null;
	}

	@Override
	public ArgContainer<? super DrawingTableBlockEntity, ?> getContainerType()
	{
		return IGMenuTypes.SCHEMATICS;
	}

	@Override
	public boolean canUseGui(Player var1)
	{
		return true;
	}

	@Override
	public @NotNull Property<Direction> getFacingProperty()
	{
		return IEProperties.FACING_HORIZONTAL;
	}

	@Override
	public BlockPos getModelOffset(BlockState blockState, Vec3i vec3i)
	{
		if(isDummy())
			return DUMMY_POS;
		else
			return MASTER_POS;
	}

	@Override
	public Component getDisplayName()
	{
		return Component.translatable("desc.immersivegeology.schematic_table");
	}
}
