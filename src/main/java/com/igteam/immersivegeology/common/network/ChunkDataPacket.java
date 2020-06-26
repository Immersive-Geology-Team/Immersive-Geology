package com.igteam.immersivegeology.common.network;

import java.util.function.Supplier;

import com.igteam.immersivegeology.common.world.chunk.data.ChunkData;
import com.igteam.immersivegeology.common.world.climate.ClimateIG;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.fml.network.NetworkEvent;

public class ChunkDataPacket {
    private int chunkX;
    private int chunkZ;
    private CompoundNBT nbt;

    public ChunkDataPacket(int chunkX, int chunkZ, ChunkData data)
    {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.nbt = data.serializeNBT();
    }

    ChunkDataPacket(PacketBuffer buffer)
    {
        chunkX = buffer.readInt();
        chunkZ = buffer.readInt();
        nbt = buffer.readCompoundTag();
    }

    void encode(PacketBuffer buffer)
    {
        buffer.writeInt(chunkX);
        buffer.writeInt(chunkZ);
        buffer.writeCompoundTag(nbt);
    }

    void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> {
            // Update client-side chunk data capability
            IChunk chunk = Minecraft.getInstance().world.getChunk(chunkX, chunkZ);
            ChunkData.get(chunk).ifPresent(data -> {
                // Update client side chunk data
                data.deserializeNBT(nbt);

                // Update climate cache
                ClimateIG.update(chunk.getPos(), data.getAvgTemp(), data.getRainfall());
            });
        });
        context.get().setPacketHandled(true);
    }
}
