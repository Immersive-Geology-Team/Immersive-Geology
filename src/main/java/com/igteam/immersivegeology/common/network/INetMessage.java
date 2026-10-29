package com.igteam.immersivegeology.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public interface INetMessage extends CustomPacketPayload
{
	void process(IPayloadContext context);
}
