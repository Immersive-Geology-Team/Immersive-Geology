package com.igteam.immersivegeology.common.block.belt;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeltModelFilesTest
{
	private static final Path OBJ_ROOT = Path.of("src/main/resources/assets/immersivegeology/models/block/belt");
	private static final Path GENERATED_ROOT = Path.of("src/generated/resources/assets/immersivegeology/models/block/belt");
	private static final double BOUNDARY_PIXELS = 0.05;
	private static final double FORGE_FLAT_NORMAL_LIMIT = 126.5/127;

	private static List<Path> files(Path root, String extension) throws IOException
	{
		try(Stream<Path> walk = Files.walk(root))
		{
			return walk.filter(p -> p.toString().endsWith(extension)).toList();
		}
	}

	@Test
	void facesOnABlockEdgeAreLitFromTheirOwnBlock() throws IOException
	{
		List<Path> objs = files(OBJ_ROOT, ".obj");
		assertFalse(objs.isEmpty());
		for(Path obj : objs)
		{
			List<double[]> vertices = new ArrayList<>();
			List<double[]> normals = new ArrayList<>();
			for(String line : Files.readAllLines(obj))
			{
				String[] parts = line.trim().split("\\s+");
				switch(parts[0])
				{
					case "v" -> vertices.add(new double[]{Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3])});
					case "vn" -> normals.add(new double[]{Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3])});
					case "f" -> {
						double[] normal = normals.get(Integer.parseInt(parts[1].split("/")[2])-1);
						for(int axis = 0; axis < 3; axis++)
						{
							double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
							for(int i = 1; i < parts.length; i++)
							{
								double value = vertices.get(Integer.parseInt(parts[i].split("/")[0])-1)[axis]*16;
								min = Math.min(min, value);
								max = Math.max(max, value);
							}
							if(max-min > 1e-6)
								continue;
							double fromEdge = Math.min(((min%16)+16)%16, 16-((min%16)+16)%16);
							if(fromEdge < BOUNDARY_PIXELS)
							{
								assertTrue(fromEdge > 1e-4*16, obj+" has a face exactly on a block edge, vanilla lighting will sample the neighbour");
								assertTrue(Math.abs(normal[axis]) < FORGE_FLAT_NORMAL_LIMIT, obj+" has an axis-aligned normal on a block-edge face, Forge lighting will sample the neighbour");
							}
							break;
						}
					}
					default -> {
					}
				}
			}
		}
	}

	@Test
	void beltModelsUseFlatLighting() throws IOException
	{
		List<Path> models = files(GENERATED_ROOT, ".json");
		assertFalse(models.isEmpty());
		for(Path model : models)
		{
			JsonObject json = JsonParser.parseString(Files.readString(model)).getAsJsonObject();
			assertTrue(json.has("ambientocclusion")&&!json.get("ambientocclusion").getAsBoolean(), model+" must disable ambient occlusion");
		}
	}
}
