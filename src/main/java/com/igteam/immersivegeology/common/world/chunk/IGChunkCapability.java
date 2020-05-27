package com.igteam.immersivegeology.common.world.chunk;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public interface IGChunkCapability {
	String getIGMarker();

    void setIGMarker(String marker);

    class Default implements IGChunkCapability {
        private String replacingMarker;

        public Default() {
            replacingMarker = null;
        }

        @Override
        public void setIGMarker(String marker) {
            replacingMarker = marker;
        }

        @Override
        public String getIGMarker() {
            return replacingMarker;
        }
    }

    class Storage implements Capability.IStorage<IGChunkCapability> {
        @Override
        public void readNBT(Capability<IGChunkCapability> capability, IGChunkCapability instance, Direction side,
                INBT nbt) {
            if (nbt instanceof StringNBT && !nbt.getString().isEmpty())
                instance.setIGMarker(nbt.getString());
        }

        @Override
        public INBT writeNBT(Capability<IGChunkCapability> capability, IGChunkCapability instance,
        Direction side) {
            return new StringNBT(instance.getIGMarker() != null ? instance.getIGMarker() : "");
        }
    }
}
