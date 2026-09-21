package com.igteam.immersivegeology.common.block.helper;

public final class OreBlockMeta
{
	public static final int RICHNESS_COUNT = OreRichness.values().length;
	public static final int WEATHERING_COUNT = MineralWeathering.values().length;
	public static final int COUNT = RICHNESS_COUNT*WEATHERING_COUNT;

	private OreBlockMeta()
	{
	}

	public static int pack(OreRichness richness, MineralWeathering weathering)
	{
		return richness.ordinal()*WEATHERING_COUNT+weathering.ordinal();
	}

	public static OreRichness richness(int meta)
	{
		return OreRichness.values()[(clamp(meta)/WEATHERING_COUNT)%RICHNESS_COUNT];
	}

	public static MineralWeathering weathering(int meta)
	{
		return MineralWeathering.values()[clamp(meta)%WEATHERING_COUNT];
	}

	private static int clamp(int meta)
	{
		return meta < 0||meta >= COUNT?0: meta;
	}
}
