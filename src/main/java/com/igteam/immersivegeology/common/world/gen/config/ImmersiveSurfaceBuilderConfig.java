package com.igteam.immersivegeology.common.world.gen.config;

import com.igteam.immersivegeology.common.world.gen.surface.ISurfaceBuilder;
import com.igteam.immersivegeology.common.world.gen.surface.ISurfacePart;

public class ImmersiveSurfaceBuilderConfig
{
	private final ISurfacePart top;
	private final ISurfacePart under;
	private final ISurfacePart underWater;
	private final ISurfacePart deepUnder;

	public ImmersiveSurfaceBuilderConfig(ISurfacePart top, ISurfacePart under, ISurfacePart underWater)
	{
		this(top, under, underWater, ISurfaceBuilder.CLAY);
	}

	public ImmersiveSurfaceBuilderConfig(ISurfacePart top, ISurfacePart under, ISurfacePart underWater, ISurfacePart deepUnder)
	{
		this.top = top;
		this.under = under;
		this.underWater = underWater;
		this.deepUnder = deepUnder;
	}

	public ISurfacePart getTop()
	{
		return top;
	}

	public ISurfacePart getUnder()
	{
		return under;
	}

	public ISurfacePart getUnderWater()
	{
		return underWater;
	}

	public ISurfacePart getDeepUnder()
	{
		return deepUnder;
	}
}
