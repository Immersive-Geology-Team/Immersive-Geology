package com.igteam.immersivegeology.core.material.helper.material;

import java.util.Locale;

public enum CrystalFamily
{
	CUBIC,
	HEXAGONAL,
	TETRAGONAL,
	ORTHORHOMBIC,
	MONOCLINIC,
	TRICLINIC;

	public String getName()
	{
		return name().toLowerCase(Locale.ROOT);
	}
}
