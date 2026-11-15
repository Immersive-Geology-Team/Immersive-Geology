package com.igteam.immersivegeology.common.network;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class IGPacketHandler
{
	public static final String NET_VERSION = "1";
	private record ProtocolMarker() implements INetMessage {
		private static final Type<ProtocolMarker> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(IGLib.MODID, "main"));
		private static final StreamCodec<RegistryFriendlyByteBuf, ProtocolMarker> CODEC = StreamCodec.unit(new ProtocolMarker());

		@Override
		public Type<ProtocolMarker> type() {
			return TYPE;
		}

		@Override
		public void process(IPayloadContext context) {
		}
	}

	private final Set<CustomPacketPayload.Type<?>> knownPacketTypes = new HashSet<>();

	private static final IGPacketHandler handler = new IGPacketHandler();

	public static void initialize(RegisterPayloadHandlersEvent event)
	{
		PayloadRegistrar registrar = event.registrar(NET_VERSION);
		handler.registerMessage(registrar, ProtocolMarker.TYPE, ProtocolMarker.CODEC);
	}

	private <T extends INetMessage> void registerMessage(PayloadRegistrar registrar, CustomPacketPayload.Type<T> packetType, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
		this.registerMessage(registrar, packetType, codec, Optional.empty());
	}

	private <T extends INetMessage> void registerMessage(PayloadRegistrar registrar, CustomPacketPayload.Type<T> packetType, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PacketFlow direction) {
		this.registerMessage(registrar, packetType, codec, Optional.of(direction));
	}

	private <T extends INetMessage> void registerMessage(PayloadRegistrar registrar, CustomPacketPayload.Type<T> packetType, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, Optional<PacketFlow> direction) {
		if (!this.knownPacketTypes.add(packetType)) {
			throw new IllegalStateException("Duplicate packet type: " + packetType.id());
		} else {
			if(direction.isEmpty())
				registrar.playBidirectional(packetType, codec, T::process);
			else if(direction.get()==PacketFlow.CLIENTBOUND)
				registrar.playToClient(packetType, codec, T::process);
			else if(direction.get()==PacketFlow.SERVERBOUND)
				registrar.playToServer(packetType, codec, T::process);
		}
	}

	/**
	 * Sends a server message directly to the player. Will not do anything if the provided instance is not a {@link ServerPlayer} instance
	 *
	 * @param player  The {@link Player} to send to
	 * @param message The message to send
	 */
	public static <MSG extends CustomPacketPayload> void sendToPlayer(Player player, @Nonnull MSG message){
		if(message != null && player instanceof ServerPlayer serverPlayer){
			PacketDistributor.sendToPlayer(serverPlayer, message);
		}
	}

	/** Client -> Server */
	public static <MSG extends CustomPacketPayload> void sendToServer(MSG message){
		if(message == null)
			return;

		PacketDistributor.sendToServer(message);
	}

	/**
	 * Sends a packet to everyone in the specified dimension.
	 *
	 * <pre>
	 * Server -> Client
	 * </pre>
	 */
	public static <MSG extends CustomPacketPayload> void sendToDimension(ServerLevel dim, MSG message){
		if(message == null)
			return;

		PacketDistributor.sendToPlayersInDimension(dim, message);
	}

	public static <MSG extends CustomPacketPayload> void sendAll(MSG message){
		if(message == null)
			return;

		PacketDistributor.sendToAllPlayers(message);
	}
}
