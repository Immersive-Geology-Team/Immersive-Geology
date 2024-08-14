package com.igteam.immersivegeology.core.material.helper.material;
public enum CrystalFamily {
    CUBIC,
    HEXAGONAL,
    TETRAGONAL,
    ORTHORHOMBIC,
    MONOCLINIC,
    TRICLINIC;

    //Notes about the Crystal stuff,
    /*
        There are SIX Crystal Families
        There are SEVEN Crystal Systems
        There are also SEVEN Crystal Lattices (this is not a 1 to 1 thing either)
        Hexagonal contains the systems Trigonal and Hexagonal
        Where the Hexagonal System has a Hexagonal Lattice, but Trigonal System
        can have either a Hexagonal Lattice or a Rhombohedral Lattice
        This subject is Confusing as it's related to Group Theory, where Trigonal
        and Hexagonal Systems are Subgroups to the Hexagonal Family!
        (those systems then generate their own subgroups)
        This system may be implemented in THE FAR FUTURE if the demand is high enough!
        For now though, I'm sticking with the six Families as a way to change item and possibly block textures
     */

    public String getName() {
        return name().toLowerCase();
    }
}