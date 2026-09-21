package com.igteam.immersivegeology.client;

import com.igteam.immersivegeology.common.block.helper.MineralWeathering;
import com.igteam.immersivegeology.common.block.helper.OreBlockMeta;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.client.pack.IGPackInjector;
import com.igteam.immersivegeology.common.item.IGOreItemBlock;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.registration.IGContent;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = IGLib.MODID, value = Side.CLIENT)
public class IGClientRegistration
{
	@SubscribeEvent
	public static void onModelRegistry(ModelRegistryEvent event)
	{
		IGPackInjector.populate();

		int registered = 0;
		for(Item item : IGContent.getRegisteredItems())
		{
			if(item.getRegistryName()==null) continue;

			if(item instanceof IGOreItemBlock)
			{
				for(OreRichness richness : OreRichness.values())
					for(MineralWeathering weathering : MineralWeathering.values())
					{
						int meta = OreBlockMeta.pack(richness, weathering);
						ResourceLocation variantModel = new ResourceLocation(IGLib.MODID,
								item.getRegistryName().getPath()
										+"_"+richness.getSanitizedName()
										+"_"+weathering.getSanitizedName());
						ModelLoader.setCustomModelResourceLocation(item, meta,
								new ModelResourceLocation(variantModel, "inventory"));
						registered++;
					}
				continue;
			}

			ModelLoader.setCustomModelResourceLocation(item, 0,
					new ModelResourceLocation(item.getRegistryName(), "inventory"));
			registered++;
		}
		IGLib.IG_LOGGER.info("- Registered {} item models", registered);
	}
}
