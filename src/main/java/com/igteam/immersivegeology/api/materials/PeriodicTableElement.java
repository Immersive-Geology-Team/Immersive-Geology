package com.igteam.immersivegeology.api.materials;

import net.minecraft.util.IStringSerializable;

/**
 * Created by Pabilo8 on 25-03-2020.
 * This enum contains all the elements of the periodic table
 */
public enum PeriodicTableElement implements IStringSerializable
{
	COPPER("Cu", 0xde8518);

	String latinName, symbol;
	//the color of the this chemical element, not the metals made of it
	int color;

	PeriodicTableElement(String symbol, int color)
	{
		this.symbol = symbol;
		this.color = color;
	}

	//For translation purposes
	@Override
	public String getName()
	{
		return toString().toLowerCase();
	}

	/**
	 * This is a class representing a chemical element inside an equation (like H2 in H2O)
	 * With quantity being 2, and the element being Hydrogen
	 */
	public static class ElementProportion
	{
		PeriodicTableElement element;
		int quantity = 1;

		public ElementProportion(PeriodicTableElement element, int quantity)
		{
			this.element = element;
			this.quantity = quantity;
		}

		public ElementProportion(PeriodicTableElement element)
		{
			this.element = element;
		}
	}
}
