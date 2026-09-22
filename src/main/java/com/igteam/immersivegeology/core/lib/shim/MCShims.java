package com.igteam.immersivegeology.core.lib.shim;

// I'll be phasing this horrid amalgamation shim class out once I've been able to carry everything over...
// for now it stays, even the vanilla stone types that don't exist in 1.12.2, I will remove them eventually.
// But for now to ease this headache I've just declared everything as a generic that we can deal with in the future.
public final class MCShims
{
	private MCShims()
	{
	}

	public static net.minecraft.item.Item oreItem(String oreName)
	{
		java.util.List<net.minecraft.item.ItemStack> ores =
				net.minecraftforge.oredict.OreDictionary.getOres(oreName, false);
		return ores.isEmpty()?net.minecraft.init.Items.AIR: ores.get(0).getItem();
	}

	public static class TagKey<T>
	{
		private final String name;

		public TagKey(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public String serialize()
		{
			return getName();
		}
	}

	public static class Biome
	{
	}

	public static class TargetBlockState
	{
	}

	public static final class OreConfiguration
	{
		public static TargetBlockState target(Object test, Object state)
		{
			return new TargetBlockState();
		}
	}

	public static final class Tags
	{
		public static final class Blocks
		{
			public static final Object STONE_ORE_REPLACEABLES = new Object();
			public static final Object STONE = new Object();
			public static final Object NETHERRACK = new Object();
			public static final Object END_STONES = new Object();
		}
	}

	public static class TagMatchTest
	{
		public TagMatchTest(Object... arguments)
		{
		}
	}

	public enum Tiers
	{
		WOOD, STONE, IRON, DIAMOND, GOLD, NETHERITE
	}

	public enum Rarity
	{
		COMMON, UNCOMMON, RARE, EPIC
	}

	public static final class NoteBlockInstrument
	{
		// 1.13+ sound types with no 1.12.2 equivalent, shimming for quick compat, I used these a lot.
		public static final Object BASEDRUM = new Object();
		public static final Object HAT = new Object();
		public static final Object SNARE = new Object();
		public static final Object BASS = new Object();
		public static final Object COW_BELL = new Object();
		public static final Object BELL = new Object();
		public static final Object CHIME = new Object();
		public static final Object XYLOPHONE = new Object();
		public static final Object IRON_XYLOPHONE = new Object();
		public static final Object DIDGERIDOO = new Object();
		public static final Object BIT = new Object();
		public static final Object BANJO = new Object();
		public static final Object PLING = new Object();
		public static final Object GUITAR = new Object();
		public static final Object FLUTE = new Object();
	}

	public static final class SoundType
	{
		public static final Object STONE = net.minecraft.block.SoundType.STONE;
		public static final Object METAL = net.minecraft.block.SoundType.METAL;
		public static final Object GLASS = net.minecraft.block.SoundType.GLASS;
		public static final Object SAND = net.minecraft.block.SoundType.SAND;
		public static final Object GROUND = net.minecraft.block.SoundType.GROUND;
		public static final Object WOOD = net.minecraft.block.SoundType.WOOD;

		// 1.13+ sound types with no 1.12.2 equivalent, shimming for quick compat, I used these a lot.
		public static final Object GILDED_BLACKSTONE = net.minecraft.block.SoundType.STONE;
		public static final Object DEEPSLATE = net.minecraft.block.SoundType.STONE;
		public static final Object CALCITE = net.minecraft.block.SoundType.STONE;
		public static final Object DRIPSTONE_BLOCK = net.minecraft.block.SoundType.STONE;
		public static final Object BASALT = net.minecraft.block.SoundType.STONE;
		public static final Object NETHERRACK = net.minecraft.block.SoundType.STONE;
		public static final Object BAMBOO_WOOD = net.minecraft.block.SoundType.WOOD;
		public static final Object COPPER = net.minecraft.block.SoundType.METAL;
		public static final Object POLISHED_DEEPSLATE = net.minecraft.block.SoundType.STONE;
		public static final Object NETHER_BRICKS = net.minecraft.block.SoundType.STONE;
	}

	public static final class ParticleTypes
	{
		public static final Object SMOKE = new Object();
		public static final Object FLAME = new Object();
		public static final Object LARGE_SMOKE = new Object();
		public static final Object CLOUD = new Object();
	}

	public static final class MobEffects
	{
		public static final Object POISON = new Object();
		public static final Object WITHER = new Object();
		public static final Object CONFUSION = new Object();
		public static final Object WEAKNESS = new Object();
		public static final Object MOVEMENT_SLOWDOWN = new Object();
	}

	public static final class Fluids
	{
		public static final Object EMPTY = new Object();
		public static final Object WATER = new Object();
		public static final Object LAVA = new Object();
	}

	public static final class Items
	{
		public static final net.minecraft.item.Item AIR = net.minecraft.init.Items.AIR;
		public static final net.minecraft.item.Item BUCKET = net.minecraft.init.Items.BUCKET;
		public static final net.minecraft.item.Item WATER_BUCKET = net.minecraft.init.Items.WATER_BUCKET;
		public static final net.minecraft.item.Item COAL = net.minecraft.init.Items.COAL;
		public static final net.minecraft.item.Item CLAY_BALL = net.minecraft.init.Items.CLAY_BALL;
		public static final net.minecraft.item.Item FLINT = net.minecraft.init.Items.FLINT;
		public static final net.minecraft.item.Item GUNPOWDER = net.minecraft.init.Items.GUNPOWDER;
		public static final net.minecraft.item.Item SUGAR = net.minecraft.init.Items.SUGAR;
		public static final net.minecraft.item.Item PAPER = net.minecraft.init.Items.PAPER;
		public static final net.minecraft.item.Item STICK = net.minecraft.init.Items.STICK;
		public static final net.minecraft.item.Item GEMS_QUARTZ = net.minecraft.init.Items.QUARTZ;
		public static final net.minecraft.item.Item SAND = net.minecraft.item.Item.getItemFromBlock(net.minecraft.init.Blocks.SAND);

	}

	public static final net.minecraft.item.Item coalCokeDust = oreItem("dustCoalCoke");

	public static final class IEItems
	{
		public static final class Misc
		{
			public static final ItemSupplier COAL_COKE = new ItemSupplier("fuelCoke");
			public static final ItemSupplier FERTILIZER = new ItemSupplier("fertilizer");
		}

		public static final class ItemSupplier
		{
			private final String oreName;

			ItemSupplier(String oreName)
			{
				this.oreName = oreName;
			}

			public net.minecraft.item.Item get()
			{
				return oreItem(oreName);
			}
		}
	}

	public static final class Ingredients
	{
		public static final net.minecraft.item.Item DUST_COKE = oreItem("dustCoke");
		public static final net.minecraft.item.Item DUST_HOP_GRAPHITE = oreItem("dustHopGraphite");
		public static final net.minecraft.item.Item DUST_SULFUR = oreItem("dustSulfur");
		public static final net.minecraft.item.Item SLAG = oreItem("slag");
		public static final net.minecraft.item.Item STICK_TREATED = oreItem("stickTreatedWood");
		public static final net.minecraft.item.Item STICK_IRON = oreItem("stickIron");
		public static final net.minecraft.item.Item STICK_STEEL = oreItem("stickSteel");
	}

	public interface ITier
	{
	}

	public static class Tier implements ITier
	{
	}

	public static class Properties
	{
		public Properties density(int value)
		{
			return this;
		}

		public Properties viscosity(int value)
		{
			return this;
		}

		public Properties temperature(int value)
		{
			return this;
		}

		public Properties lightLevel(int value)
		{
			return this;
		}

		public Properties descriptionId(String value)
		{
			return this;
		}
	}


	public static final class Blocks
	{
		public static final Object STONE = net.minecraft.init.Blocks.STONE;
		public static final net.minecraft.item.Item SAND = net.minecraft.item.Item.getItemFromBlock(net.minecraft.init.Blocks.SAND);
		public static final Object END_STONE = net.minecraft.init.Blocks.END_STONE;
		public static final Object NETHERRACK = net.minecraft.init.Blocks.NETHERRACK;
		public static final Object NETHER_BRICKS = net.minecraft.init.Blocks.NETHER_BRICK;
		public static final Object SANDSTONE = net.minecraft.init.Blocks.SANDSTONE;
		public static final Object GRAVEL = net.minecraft.init.Blocks.GRAVEL;
		public static final Object CLAY = net.minecraft.init.Blocks.CLAY;
		public static final Object OBSIDIAN = net.minecraft.init.Blocks.OBSIDIAN;
		public static final Object IRON_BLOCK = net.minecraft.init.Blocks.IRON_BLOCK;

		// 1.13+ blocks with no 1.12.2 equivalent: placeholders so declarations stay verbatim
		public static final Object ANDESITE = new Object();
		public static final Object DIORITE = new Object();
		public static final Object GRANITE = new Object();
		public static final Object BASALT = new Object();
		public static final Object DEEPSLATE = new Object();
		public static final Object POLISHED_DEEPSLATE = new Object();
		public static final Object DRIPSTONE_BLOCK = new Object();
		public static final net.minecraft.item.Item CALCITE = net.minecraft.init.Items.AIR;
		public static final Object GILDED_BLACKSTONE = new Object();
		public static final Object BAMBOO_WOOD = new Object();
		public static final Object COW_BELL = new Object();
		public static final Object NETHER = new Object();
	}

	public static class Codec<T>
	{
		public static final Codec<String> STRING = new Codec<>();

		public <R> Codec<R> xmap(java.util.function.Function<T, R> to, java.util.function.Function<R, T> from)
		{
			return new Codec<>();
		}
	}

	public static class MobEffectInstance
	{
		public MobEffectInstance(Object... arguments)
		{
		}
	}
}
