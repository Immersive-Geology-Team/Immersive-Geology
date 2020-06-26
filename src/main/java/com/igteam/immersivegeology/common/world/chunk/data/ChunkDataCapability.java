package com.igteam.immersivegeology.common.world.chunk.data;

import com.igteam.immersivegeology.common.util.NoopStorage;
import com.igteam.immersivegeology.common.world.help.Helpers;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import static com.igteam.immersivegeology.ImmersiveGeology.MODID;

/*
 * Sourced from TerraFirmaCraft Github
 * Author: alcatrazEscapee
 */

public class ChunkDataCapability {
	@CapabilityInject(ChunkData.class)
    public static final Capability<ChunkData> CAPABILITY = Helpers.getNull();
    public static final ResourceLocation KEY = new ResourceLocation(MODID, "chunk_data");

    public static void setup()
    {
        CapabilityManager.INSTANCE.register(ChunkData.class, new NoopStorage<>(), ChunkData::new);
    }
}
