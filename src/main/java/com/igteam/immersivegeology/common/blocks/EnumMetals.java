package com.igteam.immersivegeology.common.blocks;

import com.igteam.immersivegeology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersivegeology.common.materials.MaterialMetalCopper;
import net.minecraft.util.IStringSerializable;

public enum EnumMetals implements IStringSerializable
{
	Copper(new MaterialMetalCopper());

	public final MaterialMetalBase metal;

	EnumMetals(MaterialMetalBase metal)
	{
		this.metal = metal;
	}

	@Override
	public String getName()
	{
		return toString().toLowerCase();
	}

}
