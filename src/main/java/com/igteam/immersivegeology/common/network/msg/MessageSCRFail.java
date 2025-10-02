/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.network.msg;

import blusunrize.immersiveengineering.common.network.IMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class MessageSCRFail implements IMessage
{
	private final BlockPos pos;

	public MessageSCRFail(BlockPos pos)
	{
		this.pos = pos;
	}

	@Override
	public void toBytes(FriendlyByteBuf friendlyByteBuf)
	{

	}

	@Override
	public void process(Supplier<Context> supplier)
	{

	}
}
