package com.igteam.immersivegeology.common.network;

import blusunrize.immersiveengineering.common.network.IMessage;
import blusunrize.immersiveengineering.common.network.MessageMagnetEquip;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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

	private final Set<Class<?>> knownPacketTypes = new HashSet();
	private int messageId = 0;

	private static final IGPacketHandler handler = new IGPacketHandler();

	public static void initialize()
	{
	}

	private <T extends IMessage> void registerMessage(Class<T> packetType, Function<FriendlyByteBuf, T> decoder) {
		this.registerMessage(packetType, decoder, Optional.empty());
	}

	private <T extends IMessage> void registerMessage(Class<T> packetType, Function<FriendlyByteBuf, T> decoder, NetworkDirection direction) {
		this.registerMessage(packetType, decoder, Optional.of(direction));
	}

	private <T extends IMessage> void registerMessage(Class<T> packetType, Function<FriendlyByteBuf, T> decoder, Optional<NetworkDirection> direction) {
		if (!this.knownPacketTypes.add(packetType)) {
			throw new IllegalStateException("Duplicate packet type: " + packetType.getName());
		} else {
			INSTANCE.registerMessage(this.messageId++, packetType, IMessage::toBytes, decoder, (t, ctx) -> {
				t.process(ctx);
				((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
			}, direction);
		}
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
