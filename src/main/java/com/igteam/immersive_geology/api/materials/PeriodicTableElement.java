package com.igteam.immersive_geology.api.materials;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

/**
 * Created by Pabilo8 on 25-03-2020.
 * This enum contains all the elements of the periodic table
 */
public enum PeriodicTableElement implements IStringSerializable
{
	//Real elements
	HYDROGEN("H", "ffffff"),
	HELIUM("He", "d0d4d3"),
	LITHIUM("Li", "d5d5d7"),
	BERYLLIUM("Be", "708090"),
	BORON("B", "000000"),
	CARBON("C", "36454f"),
	NITROGEN("N", "ffffff"),
	OXYGEN("O", "8F8FFF"),
	FLUORINE("F", "daa520"),
	NEON("Ne", "ff073a"),
	SODIUM("Na", "0000FF"),
	MAGNESIUM("Mg", "E9EEEB"),
	ALUMINIUM("Al", "d0d5db"),
	SILICON("Si", "9599a5"),
	PHOSPHORUS("P", "a5cfc7"),
	SULFUR("S", "f2ec44"),
	CHLORINE("Cl", "cfffd2"),
	ARGON("Ar", "7DF9FF"),
	POTASSIUM("K", "fbd4ac"),
	CALCIUM("Ca", "808090"),
	SCANDIUM("Sc", "c0c0c0"),
	TITANIUM("Ti", "878681"),
	VANADIUM("V", "aaa9ad"),
	CHROMIUM("Cr", "c6c8c9"),
	MANGANESE("Mn", "c4aead"),
	IRON("Fe", "d8dada"),
	COBALT("Co", "0047ab"),
	NICKEL("Ni", "b3c1b3"),
	COPPER("Cu", "de8518"),
	ZINC("Zn", "bac4c8"),
	GALLIUM("Ga", "6699cc"),
	GERMANIUM("Ge", "aaa9ad"),
	ARSENIC("As", "3B444B"),
	SELENIUM("Se", "43464B"),
	BROMINE("Br", "a13d2d"),
	KRYPTON("Kr", "b8c0c3"),
	RUBIDIUM("Rb", "c5b9b4"),
	STRONTIUM("Sr", "D1C5BC"),
	YTTRIUM("Y", "2E5090"),
	ZIRCONIUM("Zr", "eaeded"),
	NIOBIUM("Nb", "f0f7ff"),
	MOLYBDENUM("Mo", "aaa9ad"),
	TECHNETIUM("Tc", "8c969d"),
	RUTHENIUM("Ru", "C9CBC8"),
	RHODIUM("Rh", "E2E7E1"),
	PALLADIUM("PD", "CED0DD"),
	SILVER("Ag", "e7e7f7"),
	CADMIUM("Cd", "e30022"),
	INDIUM("In", "565f6b"),
	TIN("Sn", "d3d4d5"),
	ANTIMONY("Sb", "4b4e4c"),
	TELLURIUM("Te", "aaa9ad"),
	IODINE("I", "301934"),
	XENON("Xe", "14397f"),
	CESIUM("Cs", "e6be8a"),
	BARIUM("Ba", "8eff9f"),
	LANTHANUM("La", "#bac4c8"),
	CERIUM("Ce", "D1DBDD"),
	PRASEODYMIUM("Pr", "98fb98"),
	NEODYMIUM("Nd", "aaa9ad"),
	PROMETHIUM("Pm", "8C897E"),
	SAMARIUM("Sm", "aaa9ad"),
	EUROPIUM("Eu", "aaa9ad"),
	GADOLINIUM("Gd", "aaa9ad"),
	TERBIUM("Tb", "8C897E"),
	DYSPROSIUM("Dy", "e7e7f7"),
	HOLMIUM("Ho", "A79B8B"),
	ERBIUM("Er", "aaa9ad"),
	THULIUM("Tm", "8C897E"),
	YTTERBIUM("Yb", "aaa9ad"),
	LUTETIUM("Lu", "aaa9ad"),
	HAFNIUM("Hf", "e7e7f7"),
	TANTALUM("Ta", "878681"),
	TUNGSTEN("W", "96989e"),
	RHENIUM("Re", "aaa9ad"),
	OSMIUM("OS", "9090A3"),
	IRIDIUM("Ir", "e0e2dd"),
	PLATINUM("Pt", "e5e4e2"),
	GOLD("Au", "ffd700"),
	MERCURY("Hg", "d5d2d1"),
	THALLIUM("Tl", "58f898"),
	LEAD("Pb", "444f53"),
	BISMUTH("Bi", "c4aead"),
	POLONIUM("Po", "9E8C53"),
	ASTATINE("At", "616D37"),
	RADON("Rn", "ef1010"),
	FRANCIUM("Fr", "D5DA46"),
	RADIUM("Ra", "deecb8"),
	ACTINIUM("Ac", "A6BB5F"),
	THORIUM("Th", "b2beb5"),
	PROTACTINIUM("Pa", "705B5F"),
	URANIUM("U", "759068"),
	NEPTUNIUM("Np", "e7e7f7"),
	PLUTONIUM("Pu", "7BE187"),
	AMERICIUM("Am", "aaa9ad"),
	CURIUM("Cm", "aaa9ad"),
	BERKELIUM("Bk", "aaa9ad"),
	CALIFORNIUM("Cf", "aaa9ad"),
	EINSTEINIUM("Es", "aaa9ad"),
	FERMIUM("Fm", "aaa9ad"),
	MENDELEVIUM("Md", "aaa9ad"),
	NOBELIUM("No", "aaa9ad"),
	LAWRENCIUM("Lr", "aaa9ad"),
	RUTHERFORDIUM("Rf", "aaa9ad"),
	DUBNIUM("Db", "aaa9ad"),
	SEABORGIUM("Sg", "aaa9ad"),
	BHORIUM("Bh", "aaa9ad"),
	HASSIUM("Hs", "aaa9ad"),
	MEITNERIUM("Mt", "aaa9ad"),
	DARMSTADTIUM("Ds", "aaa9ad"),
	ROENTGENIUM("Rg", "aaa9ad"),
	COPPERNICIUM("Cn", "aaa9ad"),
	NIHONIUM("Nh", "aaa9ad"),
	FLEROVIUM("Fl", "aaa9ad"),
	MOSCOVIUM("Mc", "aaa9ad"),
	LIVERMORIUM("Lv", "aaa9ad"),
	TENNESSINE("Ts", "aaa9ad"),
	OGANESSON("Og", "aaa9ad"),

	//Non-real Elements (use three letters for non real to keep them separate)
	REDNIUM("Red", "d43c2c", false),
	LUXUM("Lux", "FFBC5E", false),
	SOULITE("Sou", "49362A", false),
	UNOBTANIUM("Uno", "878681", false),
	PHLEBOTINUM("Sas", "663399", false);

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

	public String getName()
	{
		return toString().toLowerCase(Locale.ENGLISH);
	}

	public boolean isReal()
	{
		return this.isReal;
	}

	@Override
	public String getSerializedName() {
		return getName();
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
