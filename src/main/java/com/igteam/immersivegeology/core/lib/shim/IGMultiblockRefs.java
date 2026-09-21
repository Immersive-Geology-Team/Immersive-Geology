package com.igteam.immersivegeology.core.lib.shim;

// Quick n dirty shim for systems that need multiblocks "now" but don't actually do anything with them.
// This will let me check each multiblock as needed without implementing everything at once or starting from the ground up.
public final class IGMultiblockRefs
{
	private IGMultiblockRefs()
	{
	}

	public static class MultiblockRef
	{
		public static final MultiblockRef INSTANCE = new MultiblockRef();
	}

	public static final class IGAlternatorMultiblock extends MultiblockRef {}
	public static final class IGBallmillMultiblock extends MultiblockRef {}
	public static final class IGBloomeryMultiblock extends MultiblockRef {}
	public static final class IGCentrifugeMultiblock extends MultiblockRef {}
	public static final class IGChemicalReactorMultiblock extends MultiblockRef {}
	public static final class IGCoreDrillMultiblock extends MultiblockRef {}
	public static final class IGCrystalizerMultiblock extends MultiblockRef {}
	public static final class IGFoundryMultiblock extends MultiblockRef {}
	public static final class IGGeothermalExchangerMultiblock extends MultiblockRef {}
	public static final class IGGravitySeparatorMultiblock extends MultiblockRef {}
	public static final class IGPelletizerMultiblock extends MultiblockRef {}
	public static final class IGReverberationFurnaceMultiblock extends MultiblockRef {}
	public static final class IGRotaryKilnMultiblock extends MultiblockRef {}
	public static final class IGSmallChemicalReactorMultiblock extends MultiblockRef {}
	public static final class IGSteamTurbineMultiblock extends MultiblockRef {}

	public static final class RotaryKilnLogic
	{
		public static final int DEFAULT_TIME = 200;
		public static final int LV_HEAT = 1;
		public static final int MV_HEAT = 2;
		public static final int HV_HEAT = 3;
		public static final int DEFAULT_ENERGY = 10240;
		public static final int LV_HEAT_CAP = 1;
		public static final int MV_HEAT_CAP = 2;
		public static final int HV_HEAT_CAP = 3;
	}

	public static final class IGRegistrationHolder
	{
		public static Object getMB(String key)
		{
			return new Object();
		}
	}

	public static final class IGMultiblockProvider
	{
		public static final Object BALLMILL = new Object();
		public static final Object BLOOMERY = new Object();
		public static final Object ROTARYKILN = new Object();
		public static final Object REVERBERATION_FURNACE = new Object();
	}

	public static final class IGChemicalReactorSkins
	{
		public static final Skin HAZARD = new Skin();
		public static final Skin DEFAULT = new Skin();

		public static final class Skin
		{
			public net.minecraft.item.Item getItem()
			{
				return net.minecraft.init.Items.AIR;
			}
		}
	}

	public static final class TurbineFuel
	{
		public static Object addFuel(Object... arguments)
		{
			return new Object();
		}
	}

	public static final class IGWorldGen
	{
		public static Object register(Object... arguments)
		{
			return new Object();
		}
	}

	public interface IMultiblock
	{
	}

	public static final class IECrushingMethod
	{
		public static Object create(Object... arguments)
		{
			return new Object();
		}
	}
}
