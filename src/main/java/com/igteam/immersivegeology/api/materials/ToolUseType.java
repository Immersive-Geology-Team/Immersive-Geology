package com.igteam.immersivegeology.api.materials;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public enum ToolUseType
{
    HAMMER_HEAD(MaterialUseType.CHUNK, MaterialUseType.POLISHED_CHUNK, MaterialUseType.HAMMER_HEAD),
    PICKAXE_HEAD(MaterialUseType.PICKAXE_HEAD),
    AXE_HEAD(MaterialUseType.AXE_HEAD),
    SHOVEL_HEAD(MaterialUseType.SHOVEL_HEAD),
    HOE_HEAD(MaterialUseType.HOE_HEAD),
    HANDLE(MaterialUseType.HANDLE, MaterialUseType.ROD, MaterialUseType.ROUGH_ROD),
    BINDING(MaterialUseType.BINDING, MaterialUseType.WIRE, MaterialUseType.ROUGH_WIRE);

    private Set<MaterialUseType> validUseTypes;

    ToolUseType(MaterialUseType... validUseTypes)
    {
        this.validUseTypes = ImmutableSet.copyOf(validUseTypes);
    }

    public boolean isValidUseType(MaterialUseType type)
    {
        return this.validUseTypes.contains(type);
    }

    public Set<MaterialUseType> validUseTypes(ToolUseType type)
    {
        return this.validUseTypes;
    }
}
