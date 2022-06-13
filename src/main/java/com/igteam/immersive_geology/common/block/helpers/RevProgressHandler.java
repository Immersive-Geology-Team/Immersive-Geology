package com.igteam.immersive_geology.common.block.helpers;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class RevProgressHandler implements IProgress, INBTSerializable<CompoundNBT> {
    protected float rightProgress, leftProgress;

    public RevProgressHandler(float lProgress, float rProgress){
        this.rightProgress = rProgress;
        this.leftProgress = lProgress;
    }

    @Override
    public float getRightProgress() {
        return rightProgress;
    }

    @Override
    public void setRightProgress(float amount) {
        this.rightProgress = amount;
    }

    @Override
    public float getLeftProgress() {
        return leftProgress;
    }

    @Override
    public void setLeftProgress(float amount) {
        this.leftProgress = amount;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putFloat("leftProgress", getLeftProgress());
        tag.putFloat("rightProgress", getRightProgress());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        setLeftProgress(nbt.getFloat("leftProgress"));
        setRightProgress(nbt.getFloat("rightProgress"));
    }
}
