package com.igteam.immersivegeology.common.world.chunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.igteam.immersivegeology.ImmersiveGeology;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class WorldChunkChecker {
	public static final String MODID = ImmersiveGeology.MODID;

	@CapabilityInject(IGChunkCapability.class)
	public static final Capability<IGChunkCapability> IG_FIED = null;
	public static final ResourceLocation ubc_res = new ResourceLocation(MODID, "ub");

	public static void preInit(FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(IGChunkCapability.class, new IGChunkCapability.Storage(),
				IGChunkCapability.Default::new);
	}

	public static boolean hasAlreadyBeenIGfied(IChunk chunk) {
		IGChunkCapability cap = ((ICapabilityProvider) chunk).getCapability(IG_FIED).orElseThrow(null);

		return cap != null && cap.getIGMarker() != null
				&& cap.getIGMarker().toString().equals("IG");
	}

	public static void setDone(IChunk chunk) {
		IGChunkCapability cap = ((ICapabilityProvider) chunk).getCapability(IG_FIED).orElseThrow(null);

		if (cap != null)
			cap.setIGMarker("IG");
	}

	@SubscribeEvent
	public void attachChunkCaps(AttachCapabilitiesEvent<Chunk> e) {
		// assert IG_FIED != null;
		e.addCapability(ubc_res, new ICapabilitySerializable<INBT>() {
			IGChunkCapability inst = IG_FIED.getDefaultInstance();

			@Override
			public void deserializeNBT(INBT nbt) {
				IG_FIED.getStorage().readNBT(IG_FIED, inst, null, nbt);
			}

			@Override
			public INBT serializeNBT() { 
				return IG_FIED.getStorage().writeNBT(IG_FIED, inst, null);
			}

			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
				return capability == IG_FIED ? LazyOptional.of(() -> (T) inst) : LazyOptional.empty();
			}
		});
	}
}
