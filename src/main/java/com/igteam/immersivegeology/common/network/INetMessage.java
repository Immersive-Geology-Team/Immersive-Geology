package com.igteam.immersivegeology.common.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public interface INetMessage extends CustomPacketPayload
{
	void process(IPayloadContext context);
}
