package com.igteam.immersivegeology.common.network;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class IGPacketHandler
{
	public static final String NET_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(IGLib.MODID, "main"))
			.networkProtocolVersion(() -> NET_VERSION)
			.serverAcceptedVersions(NET_VERSION::equals)
			.clientAcceptedVersions(NET_VERSION::equals)
			.simpleChannel();

	public static void initialize()
	{

	}
	private static int id = 0;
	public static <T extends INetMessage> void registerMessage(Class<T> type, Function<FriendlyByteBuf, T> decoder){
		INSTANCE.registerMessage(id++, type, INetMessage::toBytes, decoder, (t, ctx) -> {
			t.process(ctx);
			ctx.get().setPacketHandled(true);
		});
	}

	/**
	 * Sends a server message directly to the player. Will not do anything if the provided instance is not a {@link ServerPlayer} instance
	 *
	 * @param player  The {@link Player} to send to
	 * @param message The message to send
	 */
	public static <MSG> void sendToPlayer(Player player, @Nonnull MSG message){
		if(message != null && player instanceof ServerPlayer serverPlayer){
			INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
		}
	}

	/** Client -> Server */
	public static <MSG> void sendToServer(MSG message){
		if(message == null)
			return;

		INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
	}

	/**
	 * Sends a packet to everyone in the specified dimension.
	 *
	 * <pre>
	 * Server -> Client
	 * </pre>
	 */
	public static <MSG> void sendToDimension(ResourceKey<Level> dim, MSG message){
		if(message == null)
			return;

		INSTANCE.send(PacketDistributor.DIMENSION.with(() -> dim), message);
	}

	public static <MSG> void sendAll(MSG message){
		if(message == null)
			return;

		INSTANCE.send(PacketDistributor.ALL.noArg(), message);
	}
}
