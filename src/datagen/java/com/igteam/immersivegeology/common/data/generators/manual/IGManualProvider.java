/*
 * Muddykat
 * Copyright (c) 2025
 *
 * This code is licensed under "GNU LESSER GENERAL PUBLIC LICENSE"
 * Details can be found in the license file in the root folder of this project
 */

package com.igteam.immersivegeology.common.data.generators.manual;

import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.igteam.immersivegeology.common.block.IGOreBlock;
import com.igteam.immersivegeology.common.block.helper.IOreBlock;
import com.igteam.immersivegeology.common.block.helper.OreRichness;
import com.igteam.immersivegeology.common.config.IGServerConfig;
import com.igteam.immersivegeology.common.config.IGServerConfig.Ores.OreConfig;
import com.igteam.immersivegeology.common.data.generators.manual.helper.IGManualType;
import com.igteam.immersivegeology.common.data.generators.manual.provider.ManualPageProvider;
import com.igteam.immersivegeology.common.data.generators.manual.provider.ManualTextProvider;
import com.igteam.immersivegeology.common.world.IWorldGenConfig;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.GeologyMaterial;
import com.igteam.immersivegeology.core.material.data.enums.StoneEnum;
import com.igteam.immersivegeology.core.material.helper.flags.BlockCategoryFlags;
import com.igteam.immersivegeology.core.material.helper.material.MaterialInterface;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class IGManualProvider implements DataProvider
{
	protected static final ExistingFileHelper.ResourceType PAGE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", "page");
	protected static final ExistingFileHelper.ResourceType TEXT = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".txt", "text");
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	protected final PackOutput output;
	protected final Function<ResourceLocation, ManualPageProvider> pageFactory;
	protected final Function<ResourceLocation, ManualTextProvider> textFactory;
	public final Map<ResourceLocation, ManualPageProvider> generatedPages = new HashMap<>();
	public final Map<ResourceLocation, ManualTextProvider> generatedTexts = new HashMap<>();
	public final ExistingFileHelper existingFileHelper;
	protected final String folder;
	protected final String modid;
	protected final Logger log = IGLib.getNewLogger();


	public IGManualProvider(PackOutput output, ExistingFileHelper existingFileHelper, String modid) {
		this.output = output;
		this.existingFileHelper = existingFileHelper;
		this.folder = "en_us";
		this.modid = modid;

		this.pageFactory = ManualPageProvider::new;
		this.textFactory = ManualTextProvider::new;
	}

	public void registerPages()
	{
		log.info("Registering Immersive Geology Manual Pages");

		for(Entry<IWorldGenConfig, OreConfig> entry : IGServerConfig.ORES.ores.entrySet())
		{
			IWorldGenConfig worldGenConfig = entry.getKey();
			if(worldGenConfig instanceof GeologyMaterial material)
			{
				OreConfig oreConfig = entry.getValue();
				String name = worldGenConfig.getName().toLowerCase();
				ManualPageProvider intro_provider = attemptPageCreation(name);
				ManualPageProvider processing_provider = attemptPageCreation(name+"_processing");

				ArrayList<ResourceLocation> intro_display_list = new ArrayList<>();
				for(StoneEnum stone : StoneEnum.values())
				{
					IOreBlock oreDisplay = material.getOreBlock(stone, OreRichness.NORMAL);
					if(stone.isStoneTypeValid())
					{
						if(oreDisplay!=null)
						{
							intro_display_list.add(new ResourceLocation(IGLib.MODID, BlockCategoryFlags.ORE_BLOCK.getRegistryKey(material, stone.instance())));
						}
					}
				}
				intro_provider.startAnchor(name + "_introduction")
						.setType(IGManualType.item_display)
						.addListElements("items", intro_display_list.toArray(new ResourceLocation[intro_display_list.size()])).closeAnchor();

				ManualTextProvider mineralIntroPage = attemptTextCreation(name).setTitle("Generation", "Generation Chance TODO convert to 'RARITY");
				StringBuilder intro_text_builder = new StringBuilder();
				buildMineralIntroPage(intro_text_builder, oreConfig, worldGenConfig, material);
				String intro_text = intro_text_builder.toString();
				mineralIntroPage.attachPage(name + "_introduction", intro_text);
			}
		}

		log.info("Completed");
	}

	private void buildMineralIntroPage(StringBuilder builder, OreConfig oreConfig, IWorldGenConfig worldGenConfig, GeologyMaterial mineral){
		String mineral_name = worldGenConfig.getName();
		String title_name = mineral_name.substring(0,1).toUpperCase() + mineral_name.substring(1).toLowerCase();
		boolean hasBiomePreffered = worldGenConfig.getPreferredBiome().isPresent();
		builder.append(title_name).append(" is a material found in the ").append(hasBiomePreffered?worldGenConfig.getPreferredBiome().get().toString(): "Anywhere").append(".\n");

		StringBuilder source_metals_builder = new StringBuilder();

		for(MaterialInterface<?> metal : mineral.getSourceMaterials()) {
			source_metals_builder.append(metal.getName() + ", ");
		}

		String heightInfo = "It Generates between Y" + oreConfig.minY.get() + " and Y" + oreConfig.maxY.get() + ", ";
		String sizeInfo = "Each deposit is of size " + oreConfig.veinSize.get() + "\n";
		String spawnChance = "It can be spawn a max of " + oreConfig.veinsPerChunk.get() + " times per chunk.\n";
		builder.append(heightInfo + sizeInfo + spawnChance);

		String source_metals = source_metals_builder.toString();
		if(!source_metals.isEmpty()) {
			String refined_source_metals = source_metals.substring(0, source_metals.lastIndexOf(","));
			if (refined_source_metals.contains(",")) {
				String lhs = refined_source_metals.substring(0, refined_source_metals.lastIndexOf(","));
				String rhs = refined_source_metals.substring(refined_source_metals.lastIndexOf(",") + 1);
				builder.append("It is a source for " + lhs + " and" + rhs + ".");
			} else {
				builder.append("It is a source for " + refined_source_metals + ".");
			}
		}
	}

	private ManualPageProvider attemptPageCreation(String path) {
		Preconditions.checkNotNull(path, "Path must not be null");
		ResourceLocation outputLoc = path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modid, path);
		this.existingFileHelper.trackGenerated(outputLoc, PAGE);
		return generatedPages.computeIfAbsent(outputLoc, pageFactory);
	}

	private ManualTextProvider attemptTextCreation(String path) {
		Preconditions.checkNotNull(path, "Path must not be null");
		ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modid, path));
		this.existingFileHelper.trackGenerated(outputLoc, TEXT);
		return generatedTexts.computeIfAbsent(outputLoc, textFactory);
	}

	private ResourceLocation extendWithFolder(ResourceLocation rl) {
		if (rl.getPath().contains("/")) {
			return rl;
		}
		if(folder.isEmpty()){
			return rl;
		}
		return new ResourceLocation(rl.getNamespace(), folder + "/" + rl.getPath());
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cachedOutput)
	{
		generatedPages.clear();
		registerPages();
		return generateAll(cachedOutput);
	}

	@Override
	public String getName() {
		return "Manual Provider";
	}

	protected void clear() {
		generatedPages.clear();
	}

	private CompletableFuture<?> generateAll(CachedOutput output) {
		CompletableFuture<?>[] futures = new CompletableFuture<?>[generatedPages.size() + generatedTexts.size()];
		int index = 0;

		// Generate JSON pages
		for (ManualPageProvider model : generatedPages.values()) {
			Path target = getPagePath(model);
			futures[index++] = DataProvider.saveStable(output, model.toJson(), target);
		}

		// Generate Text pages
		for (ManualTextProvider model : generatedTexts.values()) {
			Path target = getTextPath(model);
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					saveText(output, model.getResult(), target);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
			futures[index++] = future;
		}

		// Return a combined CompletableFuture that completes when all tasks finish
		return CompletableFuture.allOf(futures);
	}


	private void saveText(CachedOutput output, String text, Path path) throws IOException {
		byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
		output.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
	}

	private Path getPagePath(ManualPageProvider model) {
		ResourceLocation loc = model.getLocation();
		return output.getOutputFolder().resolve("assets/" + loc.getNamespace() + "/manual/" + loc.getPath() + ".json");
	}

	private Path getTextPath(ManualTextProvider model) {
		ResourceLocation loc = model.getLocation();
		return output.getOutputFolder().resolve("assets/" + loc.getNamespace() + "/manual/" + loc.getPath() + ".txt");
	}
}
