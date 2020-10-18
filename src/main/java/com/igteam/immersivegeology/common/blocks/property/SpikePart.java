package com.igteam.immersivegeology.common.blocks.property;

import net.minecraft.util.IStringSerializable;

public enum SpikePart implements IStringSerializable {
    base,
    middle,
    top,
    tip;

    @Override
    public String getName() {
        return this.name();
    }
}
