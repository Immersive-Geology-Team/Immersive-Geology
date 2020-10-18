package com.igteam.immersivegeology.api.materials;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public enum MaterialCrystalStructure
{
	ISOMETRIC,
	TETRAGONAL,
	ORTHORHOMBIC,
	HEXAGONAL,
	TRICLINIC,
	MONOCLINIC;

	private LatticeStructure latticeType;

	public LatticeStructure get_lattice_type()
	{
		return this.latticeType;
	}

	public void set_lattice_type(LatticeStructure type)
	{
		this.latticeType = type;
	}

	public enum LatticeStructure
	{
		//ISOMETRIC
		octahedrons,
		dodecahedrons,
		cubes,

		//TETRAGONAL
		prisims,
		double_pyramids,

		//ORTHORHOMBIC
		rhombic_prisms,
		dipyramids,

		//HEXAGONAL
		trigonals,
		hexagonals,
		rhombohedrals,

		//TRICLINIC
		pedials,
		pinacoidals,

		//MONOCLINIC
		sphenoidals,
		domatics,
		prismatics;
	}
}
