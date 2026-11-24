/*
 * Muddykat
 * Copyright (c) 2026
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.block.belt.entity;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.ConnectorBlockEntityHelper;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.LocalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.redstone.IRedstoneConnector;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import com.igteam.immersivegeology.common.block.belt.IGPoweredBeltBlock;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class IGPoweredBeltBlockEntity extends IGBeltBlockEntity implements IRedstoneConnector
{
	private static final String CHANNEL = "redstone_channel";
	private static final String WIRE_SIGNAL = "wire_signal";

	private DyeColor channel = DyeColor.WHITE;
	private int wireSignal;
	private boolean inputDirty;
	private boolean unloaded;
	@Nullable
	private GlobalWireNetwork globalNet;

	public IGPoweredBeltBlockEntity(BlockPos pos, BlockState state)
	{
		super(IGRegistrationHolder.POWERED_BELT.get(), pos, state);
	}

	public DyeColor channel()
	{
		return channel;
	}

	public void setChannel(DyeColor channel)
	{
		if(this.channel!=channel)
		{
			this.channel = channel;
			setChanged();
			inputDirty = true;
		}
	}

	public int wireSignal()
	{
		return wireSignal;
	}

	public void markInputDirty()
	{
		inputDirty = true;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, IGPoweredBeltBlockEntity belt)
	{
		if(belt.inputDirty&&belt.globalNet!=null)
		{
			belt.inputDirty = false;
			RedstoneNetworkHandler handler = belt.globalNet.getLocalNet(pos).getHandler(RedstoneNetworkHandler.ID, RedstoneNetworkHandler.class);
			if(handler!=null)
				handler.updateValues();
		}
	}

	public void onSwitchMoved()
	{
		if(globalNet==null||level==null||level.isClientSide)
			return;
		ConnectionPoint point = connectionPoint();
		LocalWireNetwork local = globalNet.getNullableLocalNet(point);
		if(local==null)
			return;
		for(Connection connection : List.copyOf(local.getConnections(point)))
			if(!connection.isInternal())
				globalNet.updateCatenaryData(connection);
	}

	private ConnectionPoint connectionPoint()
	{
		return new ConnectionPoint(worldPosition, 0);
	}

	@Override
	public void setLevel(@NotNull Level level)
	{
		super.setLevel(level);
		globalNet = GlobalWireNetwork.getNetwork(level);
	}

	@Override
	public void onLoad()
	{
		super.onLoad();
		unloaded = false;
		ConnectorBlockEntityHelper.onChunkLoad(this, level);
	}

	@Override
	public void onChunkUnloaded()
	{
		super.onChunkUnloaded();
		unloaded = true;
		if(globalNet!=null)
			ConnectorBlockEntityHelper.onChunkUnload(globalNet, this);
	}

	@Override
	public void setRemoved()
	{
		super.setRemoved();
		if(!unloaded&&level!=null)
			ConnectorBlockEntityHelper.remove(level, this);
	}

	@Override
	public boolean canConnect()
	{
		return true;
	}

	@Override
	public BlockPos getConnectionMaster(WireType type, TargetingInfo target)
	{
		return worldPosition;
	}

	@Override
	public boolean canConnectCable(WireType type, ConnectionPoint target, Vec3i offset)
	{
		return WireType.REDSTONE_CATEGORY.equals(type.getCategory());
	}

	@Override
	public void connectCable(WireType type, ConnectionPoint target, IImmersiveConnectable other, ConnectionPoint otherTarget)
	{
		setChanged();
	}

	@Override
	public ConnectionPoint getTargetedPoint(TargetingInfo info, Vec3i offset)
	{
		return connectionPoint();
	}

	@Override
	public void removeCable(Connection connection, ConnectionPoint attachedPoint)
	{
		setChanged();
	}

	@Override
	public Vec3 getConnectionOffset(ConnectionPoint here, ConnectionPoint other, WireType type)
	{
		BlockState state = getBlockState();
		if(state.getBlock() instanceof IGPoweredBeltBlock powered)
			return powered.wirePoint(state);
		return new Vec3(.5, .5, .5);
	}

	@Override
	public Collection<ConnectionPoint> getConnectionPoints()
	{
		return List.of(connectionPoint());
	}

	@Override
	public BlockPos getPosition()
	{
		return worldPosition;
	}

	@Override
	public Collection<ResourceLocation> getRequestedHandlers()
	{
		return List.of(RedstoneNetworkHandler.ID);
	}

	@Override
	public void onChange(ConnectionPoint point, RedstoneNetworkHandler handler)
	{
		if(level==null||level.isClientSide||isRemoved())
			return;
		int signal = handler.getValue(channel.getId());
		if(signal!=wireSignal)
		{
			wireSignal = signal;
			setChanged();
			if(getBlockState().getBlock() instanceof IGPoweredBeltBlock powered)
				powered.updatePowered(level, worldPosition);
		}
	}

	@Override
	public void updateInput(byte[] signals, ConnectionPoint point)
	{
		if(level!=null)
		{
			int local = level.getBestNeighborSignal(worldPosition);
			signals[channel.getId()] = (byte)Math.max(signals[channel.getId()], local);
		}
	}

	@Override
	public void load(@NotNull CompoundTag tag)
	{
		super.load(tag);
		channel = DyeColor.byId(tag.getInt(CHANNEL));
		wireSignal = tag.getInt(WIRE_SIGNAL);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		tag.putInt(CHANNEL, channel.getId());
		tag.putInt(WIRE_SIGNAL, wireSignal);
	}
}
