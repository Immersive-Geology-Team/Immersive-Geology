package com.igteam.immersivegeology.api.materials;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

/**
 * Created by Pabilo8 on 25-03-2020.
 * This enum contains all the elements of the periodic table
 */
public enum PeriodicTableElement implements IStringSerializable
{
	//TODO: recolour elements
	ALUMINIUM("Al", "d0d5db"),
	COPPER("Cu", "de8518"),
	GOLD("Au", "ffd700"),
	IRON("Fe", "d8dada"),
	LEAD("Pb", "444f53"),
	NICKEL("Ni", "b3c1b3"),
	SILVER("Ag", "e7e7f7"),
	URANIUM("U", "759068");

	String symbol;
	//the color of the this chemical element, not the metals made of it
	String color;
	//For any elements that aren't real
	boolean isReal;

	public String getSymbol()
	{
		return symbol;
	}

	public String getColor()
	{
		return color;
	}

	PeriodicTableElement(String symbol, String color, boolean isReal)
	{
		this.symbol = symbol;
		this.color = color;
		this.isReal = isReal;
	}

	PeriodicTableElement(String symbol, String color)
	{
		this.symbol = symbol;
		this.color = color;
		this.isReal = true;
	}

	//For translation purposes
	@Override
	public String getName()
	{
		return toString().toLowerCase(Locale.ENGLISH);
	}

	/**
	 * This is a class representing a chemical element inside an equation (like H2 in H2O)
	 * With quantity being 2, and the element being Hydrogen
	 */
	public static class ElementProportion
	{
		PeriodicTableElement element;
		int quantity = 1;

		public PeriodicTableElement getElement()
		{
			return element;
		}

		public int getQuantity()
		{
			return quantity;
		}

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
