package com.igteam.immersivegeology.core.material.helper.material.recipe.helper;

public class FluidTagInput
{
	private final Object tag;
	private final int amount;

	public FluidTagInput(Object tag, int amount)
	{
		this.tag = tag;
		this.amount = amount;
	}

	public Object getTag()
	{
		return tag;
	}

	public Object serialize()
	{
		return String.valueOf(tag)+"@"+amount;
	}

	public int getAmount()
	{
		return amount;
	}
}
