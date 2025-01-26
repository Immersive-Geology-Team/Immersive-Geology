package com.igteam.immersivegeology.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public interface INetMessage
{
	void toBytes(FriendlyByteBuf buf);
	void process(Supplier<Context> context);
}
