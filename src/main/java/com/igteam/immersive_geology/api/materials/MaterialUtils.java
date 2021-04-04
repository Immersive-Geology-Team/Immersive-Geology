package com.igteam.immersive_geology.api.materials;

import java.util.stream.Stream;

/**
 * @author Pabilo8
 * @since 15.07.2020
 */
public class MaterialUtils
{
	/**
	 * Creates a name for a material block
	 *
	 * @param sub-name  of the block
	 * @param type      of the block
	 * @param materials the block is made of
	 * @return name of the block in format subnameblock_type_mat1_mat2
	 */
	public static String generateMaterialName(String sub, MaterialUseType type, Material... materials)
	{
		StringBuilder stringBuilder = new StringBuilder();
		if(!sub.isEmpty())
			stringBuilder.append(sub);
		stringBuilder.append('_').append(type.getName());
		Stream.of(materials).forEachOrdered(material1 -> stringBuilder.append('_').append(material1.getName()));
		return stringBuilder.toString();
	}
}
