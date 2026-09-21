package com.igteam.immersivegeology.common.world.features;

public class IGOreFeature
{
	public static class IGOreFeatureConfig
	{
		public IGOreFeatureConfig(Object... arguments)
		{
		}

		public static long hash(String value)
		{
			return value==null?0L: value.hashCode();
		}
	}
}
