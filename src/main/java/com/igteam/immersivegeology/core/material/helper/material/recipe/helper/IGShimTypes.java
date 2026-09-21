package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

public final class IGShimTypes
{
	private IGShimTypes()
	{
	}

	public static final class Pair<A, B>
	{
		private final A first;
		private final B second;

		private Pair(A first, B second)
		{
			this.first = first;
			this.second = second;
		}

		public static <A, B> Pair<A, B> of(A first, B second)
		{
			return new Pair<>(first, second);
		}

		public A getFirst()
		{
			return first;
		}

		public B getSecond()
		{
			return second;
		}
	}

	public static final class FluidStackShim
	{
		public static final Object EMPTY = new Object();
	}

	public static final class TurbineFuel
	{
		public static Object addFuel(Object... arguments)
		{
			return new Object();
		}
	}
}
