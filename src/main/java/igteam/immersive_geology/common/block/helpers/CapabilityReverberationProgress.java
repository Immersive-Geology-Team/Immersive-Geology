package igteam.immersive_geology.common.block.helpers;


import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityReverberationProgress {
    @CapabilityInject(IProgress.class)
    public static Capability<IProgress> ReverberationProgress = null;

    public static void register(){
        CapabilityManager.INSTANCE.register(IProgress.class, new Capability.IStorage<IProgress>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IProgress> capability, IProgress instance, Direction side) {
                CompoundNBT data = new CompoundNBT();
                data.putFloat("leftProgress", instance.getLeftProgress());
                data.putFloat("rightProgress", instance.getRightProgress());
                return data;
            }

            @Override
            public void readNBT(Capability<IProgress> capability, IProgress instance, Direction side, INBT nbt) {
                CompoundNBT data = (CompoundNBT) nbt;
                instance.setLeftProgress(data.getFloat("leftProgress"));
                instance.setRightProgress(data.getFloat("rightProgress"));
            }
        }, () -> new RevProgressHandler(0, 0));
    }
}

