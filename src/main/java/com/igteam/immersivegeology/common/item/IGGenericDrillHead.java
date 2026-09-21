package com.igteam.immersivegeology.common.item;

import com.igteam.immersivegeology.core.material.helper.material.IGTag;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class IGGenericDrillHead
{
	public static class DrillHeadProps
	{
		public final String name;
		public final IGTag repairMaterial;
		public final int drillSize;
		public final int drillDepth;
		public final Object drillLevel;
		public final float drillSpeed;
		public final float drillAttack;
		public final int maxDamage;
		public final Supplier<ResourceLocation> texture;

		public DrillHeadProps(String name, IGTag repairMaterial, int drillSize, int drillDepth, Object drillLevel,
							  float drillSpeed, float drillAttack, int maxDamage, Supplier<ResourceLocation> texture)
		{
			this.name = name;
			this.repairMaterial = repairMaterial;
			this.drillSize = drillSize;
			this.drillDepth = drillDepth;
			this.drillLevel = drillLevel;
			this.drillSpeed = drillSpeed;
			this.drillAttack = drillAttack;
			this.maxDamage = maxDamage;
			this.texture = texture;
		}
	}
}
