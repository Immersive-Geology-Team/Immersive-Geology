package com.igteam.immersivegeology.client.pack;

import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.List;

public final class IGPackInjector
{
	private static IGGeneratedPack pack;

	private IGPackInjector()
	{
	}

	public static IGGeneratedPack getPack()
	{
		return pack;
	}

	private static boolean populated = false;

	public static void inject()
	{
		if(pack!=null) return;

		pack = new IGGeneratedPack();

		List<IResourcePack> defaultPacks = findDefaultPacks();
		if(defaultPacks==null)
		{
			IGLib.IG_LOGGER.error("Could not reach Minecraft's default resource pack list, textures will be missing");
			return;
		}

		defaultPacks.add(pack);
		IGLib.IG_LOGGER.info("- Reserved generated resource pack slot");
	}

	public static void populate()
	{
		if(pack==null||populated) return;
		populated = true;
		IGAssetBuilder.build(pack);
		if(Boolean.getBoolean("ig.dumpGeneratedAssets"))
		{
			java.io.File dir = new java.io.File("ig_generated_dump");
			pack.dumpTo(dir);
			IGLib.IG_LOGGER.info("- Dumped generated assets to {}", dir.getAbsolutePath());
		}
	}

	public static void populateAndReload()
	{
		if(pack==null) return;
		populate();
		IGLib.IG_LOGGER.info("- Reloading resources so generated assets are picked up");
		Minecraft.getMinecraft().refreshResources();
	}

	@SuppressWarnings("unchecked")
	private static List<IResourcePack> findDefaultPacks()
	{
		try
		{
			return ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(),
					"defaultResourcePacks", "field_110449_ao");
		} catch(Exception exception)
		{
			IGLib.IG_LOGGER.error("Reflection on defaultResourcePacks failed: {}", exception.toString());
			return null;
		}
	}
}
