/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.network;

import com.igteam.immersivegeology.common.item.blueprint.IGBlueprintSettings;
import com.igteam.immersivegeology.core.material.data.enums.MiscEnum;
import com.igteam.immersivegeology.core.material.helper.flags.ItemCategoryFlags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class BlueprintMessage implements INetMessage
{

	public static void sendToServer(IGBlueprintSettings settings, InteractionHand hand){
		IGPacketHandler.sendToServer(new BlueprintMessage(settings, hand, true));
	}

	public static void sendToClient(Player player, IGBlueprintSettings settings, InteractionHand hand){
		IGPacketHandler.sendToPlayer(player, new BlueprintMessage(settings, hand, false));
	}

	boolean forServer;
	CompoundTag nbt;
	InteractionHand hand;

	public BlueprintMessage(IGBlueprintSettings settings, InteractionHand hand, boolean toServer)
	{
		this(settings.toNbt(), hand, toServer);
	}

	public BlueprintMessage(CompoundTag nbt, InteractionHand hand, boolean toServer){
		this.nbt = nbt;
		this.forServer = toServer;
		this.hand = hand;
	}

	public BlueprintMessage(FriendlyByteBuf buf){
		this.nbt = buf.readNbt();
		this.forServer = buf.readBoolean();
		this.hand = InteractionHand.values()[buf.readByte()];
	}

	@Override
	public void toBytes(FriendlyByteBuf buf)
	{
		buf.writeNbt(this.nbt);
		buf.writeBoolean(this.forServer);
		buf.writeByte(this.hand.ordinal());
	}

	@Override
	public void process(Supplier<Context> context)
	{
		context.get().enqueueWork(() -> {
			NetworkEvent.Context con = context.get();

			if(con.getDirection().getReceptionSide() == getSide() && con.getSender() != null){
				Player player = con.getSender();
				ItemStack held = player.getItemInHand(this.hand);

				if(held.is(MiscEnum.Blueprint.getItem(ItemCategoryFlags.BLUEPRINT))){
					IGBlueprintSettings settings = new IGBlueprintSettings(this.nbt);
					settings.applyTo(held);
				}
			}
		});
	}

	LogicalSide getSide(){
		return this.forServer ? LogicalSide.SERVER : LogicalSide.CLIENT;
	}
}
