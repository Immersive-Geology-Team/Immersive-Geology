package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public final class IEPlaceholders
{
	private IEPlaceholders()
	{
	}

	public static final class IEMultiblocks
	{
		public static final Object CRUSHER = new Object();
		public static final Object ARC_FURNACE = new Object();
		public static final Object BLAST_FURNACE = new Object();
		public static final Object ALLOY_SMELTER = new Object();
		public static final Object COKE_OVEN = new Object();
		public static final Object METAL_PRESS = new Object();
		public static final Object MIXER = new Object();
		public static final Object REFINERY = new Object();
		public static final Object SQUEEZER = new Object();
		public static final Object FERMENTER = new Object();
		public static final Object BOTTLING_MACHINE = new Object();
		public static final Object ASSEMBLER = new Object();
		public static final Object EXCAVATOR = new Object();
		public static final Object SILO = new Object();
		public static final Object TANK = new Object();
		public static final Object ADVANCED_BLAST_FURNACE = new Object();
		public static final Object FEEDTHROUGH = new Object();
		public static final Object SHEETMETAL_TANK = new Object();
		public static final Object LIGHTNING_ROD = new Object();
		public static final Object DIESEL_GENERATOR = new Object();
		public static final Object BUCKET_WHEEL = new Object();
		public static final Object AUTO_WORKBENCH = new Object();
		public static final Object SAWMILL = new Object();
	}

	public static final class IETags
	{
		public static final Object fluidResin = new Object();
		public static final Object fluidCreosote = new Object();
		public static final Object fluidEthanol = new Object();
		public static final Object fluidBiodiesel = new Object();
		public static final Object fluidPlantoil = new Object();
		public static final Object fluidConcrete = new Object();
		public static final Object coalCokeDust = new Object();
		public static final Object coalCoke = new Object();
	}

	public enum EnumMetals
	{
		COPPER,
		ALUMINUM,
		LEAD,
		SILVER,
		NICKEL,
		URANIUM,
		CONSTANTAN,
		ELECTRUM,
		STEEL,
		IRON,
		GOLD;

		public String tagName()
		{
			String lower = name().toLowerCase(java.util.Locale.ROOT);
			return Character.toUpperCase(lower.charAt(0))+lower.substring(1);
		}
	}

	/**
	 * IE 1.12.2 has no IEItems class and no per-metal item fields; it uses one metadata-subtyped
	 * itemMetal. These resolve through the OreDictionary instead, which is how 1.12.2 mods share
	 * metal items and stays correct whether IE, vanilla or another mod supplies them.
	 */
	public static final class Metals
	{
		public static final MetalLookup INGOTS = new MetalLookup("ingot");
		public static final MetalLookup NUGGETS = new MetalLookup("nugget");
		public static final MetalLookup DUSTS = new MetalLookup("dust");
		public static final MetalLookup PLATES = new MetalLookup("plate");

		public static final class MetalLookup
		{
			private final String prefix;

			private MetalLookup(String prefix)
			{
				this.prefix = prefix;
			}

			public Item get(EnumMetals metal)
			{
				List<ItemStack> ores = OreDictionary.getOres(prefix+metal.tagName(), false);
				return ores.isEmpty()?Items.AIR: ores.get(0).getItem();
			}
		}
	}

	public static final class FluidTags
	{
		public static final Object WATER = new Object();
		public static final Object LAVA = new Object();
	}
}
