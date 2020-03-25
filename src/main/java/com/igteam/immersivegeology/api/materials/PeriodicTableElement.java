package com.igteam.immersivegeology.api.materials;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public enum PeriodicTableElement
{
	COPPER("Cuprum", "Cu", 0xde8518);

	String englishName, latinName, symbol;
	int color;

	PeriodicTableElement(String latinName, String symbol, int color)
	{
		this.latinName = latinName;
		this.symbol = symbol;
		this.color = color;
	}
}
