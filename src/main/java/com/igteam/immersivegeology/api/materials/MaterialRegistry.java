package com.igteam.immersivegeology.api.materials;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

/**
 * Created by Pabilo8 on 25-03-2020.
 */
public class MaterialRegistry
{
	private static ArrayList<Material> materials = new ArrayList<>();

	@Nullable
	public static Material getMaterialByName(String name)
	{
		Optional<Material> material = materials.stream().filter(mat -> mat.getName().equals(name)).findAny();
		return material.orElse(null);
	}

	public static void addMaterial(Material material)
	{
		materials.add(material);
	}

	public static void removeMaterialByName(String name)
	{
		materials.removeIf(material -> material.getName().equals(name));
	}

	public static Iterator<Material> iterate()
	{
		return materials.iterator();
	}
}
