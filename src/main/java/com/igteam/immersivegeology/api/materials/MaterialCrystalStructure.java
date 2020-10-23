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

	public MaterialCrystalStructure getParentStructure(LatticeStructure structure){
		switch(structure){
			case prisims:
			case double_pyramids:
				return TETRAGONAL;
			case rhombic_prisms:
			case dipyramids:
				return ORTHORHOMBIC;
			case trigonals:
			case hexagonals:
			case rhombohedrals:
				return HEXAGONAL;
			case pedials:
			case pinacoidals:
				return TRICLINIC;
			case sphenoidals:
			case domatics:
			case prismatics:
				return MONOCLINIC;
			case octahedrons:
			case dodecahedrons:
			case cubes:
			default:				//if the lattice structure isn't listed, return Isometric as a default
				return ISOMETRIC;
		}
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
