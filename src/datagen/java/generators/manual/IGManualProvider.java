package generators.manual;

import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.ManualHelper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import generators.manual.helper.IGManualType;
import generators.manual.providers.ManualPageProvider;
import generators.manual.providers.ManualTextProvider;
import igteam.api.IGApi;
import igteam.api.config.IGOreConfig;
import igteam.api.materials.MetalEnum;
import igteam.api.materials.MineralEnum;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.data.metal.MaterialBaseMetal;
import igteam.api.materials.data.mineral.MaterialBaseMineral;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.IGProcessingMethod;
import igteam.api.processing.helper.RecipeMethod;
import igteam.api.processing.methods.*;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.common.data.tags.ItemTagProvider;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class IGManualProvider implements IDataProvider {
    protected static final ExistingFileHelper.ResourceType PAGE = new ExistingFileHelper.ResourceType(ResourcePackType.CLIENT_RESOURCES, ".json", "page");
    protected static final ExistingFileHelper.ResourceType TEXT = new ExistingFileHelper.ResourceType(ResourcePackType.CLIENT_RESOURCES, ".txt", "text");
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final DataGenerator generator;
    protected final Function<ResourceLocation, ManualPageProvider> pageFactory;
    protected final Function<ResourceLocation, ManualTextProvider> textFactory;
    @VisibleForTesting
    public final Map<ResourceLocation, ManualPageProvider> generatedPages = new HashMap<>();

    @VisibleForTesting
    public final Map<ResourceLocation, ManualTextProvider> generatedTexts = new HashMap<>();
    @VisibleForTesting
    public final ExistingFileHelper existingFileHelper;
    protected final String folder;
    protected final String modid;

    protected final Logger log = IGApi.getNewLogger();

    public IGManualProvider(DataGenerator generator, ExistingFileHelper existingFileHelper, String modid) {
        Preconditions.checkNotNull(generator);
        this.generator = generator;

        Preconditions.checkNotNull(existingFileHelper);
        this.existingFileHelper = existingFileHelper;

        this.folder = "en_us";
        this.modid = modid;

        this.pageFactory = ManualPageProvider::new;
        this.textFactory = ManualTextProvider::new;
    }

    public void registerPages(){
        log.info("Registering IG Manual Pages");
        for(MaterialInterface<MaterialBaseMineral> mineral : MineralEnum.values()) {
            String mineral_name = mineral.getName();
            String title_name = mineral_name.substring(0,1).toUpperCase() + mineral_name.substring(1).toLowerCase();

            ManualPageProvider intro_provider = attemptPageCreation(mineral_name);
            ManualPageProvider processing_provider = attemptPageCreation(mineral_name + "_processing");

            ArrayList<ResourceLocation> intro_display_list = new ArrayList<>();

            for(StoneEnum stone : StoneEnum.values()) {
                Block oreDisplay = stone.getBlock(BlockPattern.ore, mineral);
                if (oreDisplay != null) {
                    Item oreItem = oreDisplay.asItem();
                    intro_display_list.add(oreDisplay.asItem().getRegistryName());
                }
            }

            intro_provider.startAnchor(mineral_name + "_introduction")
                    .setType(IGManualType.item_display)
                    .addListElements("items", intro_display_list.toArray(new ResourceLocation[intro_display_list.size()])).closeAnchor();

            ManualTextProvider mineralIntroPage = attemptTextCreation(mineral_name).setTitle(title_name, mineral.instance().getRarity().name());

            StringBuilder intro_text_builder = new StringBuilder();
            buildMineralIntroPage(intro_text_builder, mineral);
            String intro_text = intro_text_builder.toString();
            mineralIntroPage.attachPage(mineral_name + "_introduction", intro_text);
            Set<IGProcessingStage> stages = mineral.instance().getStages();

            for (IGProcessingStage stage : stages) {
                String stage_type = stage.getStageName();
                ManualTextProvider stageProcessMethod = attemptTextCreation(mineral_name + "_processing").setTitle(title_name + " Processing", "Factoring Factories");

                for (IGProcessingMethod method : stage.getMethods()) {
                    ManualPageProvider.ManualPageAnchor anchor = processing_provider.startAnchor(mineral_name + "_" + stage_type + "_" + method.getRecipeType().name().toLowerCase())
                            .setType(IGManualType.item_display);

                    StringBuilder processing_text = new StringBuilder(mineral_name);

                    if (method instanceof IGBlastingMethod) {
                        IGBlastingMethod blastingMethod = (IGBlastingMethod) method;
                        anchor.setType(IGManualType.crafting);
                        anchor.addListElements("recipes", method.getLocation());
                        processing_text.append(" can be processed into " + blastingMethod.getOutput().getItem().getName());
                    } else
                    if (method instanceof IGCraftingMethod) {
                        IGCraftingMethod craftingMethod = (IGCraftingMethod) method;
                        anchor.addListElements("items", Items.APPLE.getRegistryName());
                        //TODO Make Crafting Page Type
                        processing_text.append("Crafting Recipe for " + craftingMethod.getResult().getName().getString());
                    } else
                    if (method instanceof IGArcSmeltingMethod) {
                        IGArcSmeltingMethod arcSmeltingMethod = (IGArcSmeltingMethod) method;
                        anchor.addListElements("items", Items.APPLE.getRegistryName());

                        processing_text.append("Arc Smelter Recipe for " + arcSmeltingMethod.getOutput().getDisplayName().getString());
                    } else
                    if (method instanceof IGChemicalMethod) {
                        IGChemicalMethod chemicalMethod = (IGChemicalMethod) method;
                        anchor.addListElements("items", Items.APPLE.getRegistryName());

                        processing_text.append("Chemical Vat Recipe for " + chemicalMethod.getItemResult().getDisplayName().toString() + " and " + chemicalMethod.getFluidResult().getDisplayName().getString());
                    } else
                    if (method instanceof IGCalcinationMethod) {
                        IGCalcinationMethod calcinationMethod = (IGCalcinationMethod) method;
                        anchor.addListElements("items", Items.APPLE.getRegistryName());

                    } else
                    if (method instanceof IGBloomeryMethod) {
                        IGBloomeryMethod bloomeryMethod = (IGBloomeryMethod) method;
                        anchor.addListElements("items", Items.APPLE.getRegistryName());

                    } else
                    if (method instanceof IGCrystallizationMethod) {
                        IGCrystallizationMethod crystallizationMethod = (IGCrystallizationMethod) method;
                        anchor.addListElements("items", Items.APPLE.getRegistryName());

                    } else
                    if (method instanceof IGHydrojetMethod) {
                        IGHydrojetMethod hydrojetMethod = (IGHydrojetMethod) method;
                        anchor.addListElements("items", Items.APPLE.getRegistryName());

                    } else
                    if (method instanceof IGBasicSmeltingMethod) {
                        IGBasicSmeltingMethod basicSmeltingMethod = (IGBasicSmeltingMethod) method;
                        anchor.addListElements("items", Items.APPLE.getRegistryName());

                    } else
                    if (method instanceof IGRefineryMethod) {
                        IGRefineryMethod refineryMethod = (IGRefineryMethod) method;
                        anchor.addListElements("items", Items.APPLE.getRegistryName());

                    } else
                    if (method instanceof IGRoastingMethod) {
                        IGRoastingMethod roastingMethod = (IGRoastingMethod) method;
                        anchor.addListElements("items", Items.APPLE.getRegistryName());

                    } else
                    if (method instanceof IGSeparatorMethod) {
                        IGSeparatorMethod separatorMethod = (IGSeparatorMethod) method;
                        anchor.addListElements("items", Items.APPLE.getRegistryName());

                    } else if(method instanceof IGCrushingMethod){
                        IGCrushingMethod crushingMethod = (IGCrushingMethod) method;
                        anchor.addListElements("items", Items.APPLE.getRegistryName());
                    }
                    anchor.closeAnchor();

                    stageProcessMethod.attachPage(mineral_name + "_" + stage_type + "_" + method.getRecipeType().name().toLowerCase(), processing_text.toString());
                }
            }
        }
    }

    private void buildMineralIntroPage(StringBuilder builder, MaterialInterface<MaterialBaseMineral> mineral){
        String mineral_name = mineral.getName();
        String title_name = mineral_name.substring(0,1).toUpperCase() + mineral_name.substring(1).toLowerCase();

        builder.append(title_name + " is a material found in the " + mineral.getDimension().name() + ".\n");

        StringBuilder source_metals_builder = new StringBuilder();

        for(MaterialInterface<?> metal : mineral.instance().getSourceMaterials()) {
            source_metals_builder.append(metal.getName() + ", ");
        }

        IFeatureConfig config = mineral.getGenerationConfig();

        if(config instanceof IGOreConfig){
            IGOreConfig oreConfig = (IGOreConfig) config;
            String heightInfo = "It Generates between Y" + oreConfig.minY.get() + " and Y" + oreConfig.maxY.get() + ", ";
            String sizeInfo = "each deposit is between " + oreConfig.veinSizeMin.get() + " and " + oreConfig.veinSizeMax.get() + " blocks.\n";
            String spawnChance = "It can be spawn a max of " + oreConfig.veinsPerChunk.get() + " times per chunk.\n";
            builder.append(heightInfo + sizeInfo + spawnChance);
        }

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
    public void act(DirectoryCache cache) throws IOException {
        clear();
        registerPages();
        generateAll(cache);
    }

    @Override
    public String getName() {
        return "Manual Provider";
    }

    protected void clear() {
        generatedPages.clear();
    }

    protected void generateAll(DirectoryCache cache) {
        for (ManualPageProvider model : generatedPages.values()) {
            Path target = getPagePath(model);
            try {
                IDataProvider.save(GSON, cache, model.toJson(), target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for (ManualTextProvider model : generatedTexts.values()) {
            Path target = getTextPath(model);
            try {
                String text = model.getResult();
                saveText(text, target, cache);
                
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveText(String s, Path pathIn, DirectoryCache cache) throws IOException {
        String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
        if (!Objects.equals(cache.getPreviousHash(pathIn), s1) || !Files.exists(pathIn)) {
            Files.createDirectories(pathIn.getParent());

            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(pathIn)) {
                bufferedwriter.write(s);
            }
        }

        cache.recordHash(pathIn, s1);
    }

    private Path getPagePath(ManualPageProvider model) {
        ResourceLocation loc = model.getLocation();
        return generator.getOutputFolder().resolve("assets/" + loc.getNamespace() + "/manual/" + loc.getPath() + ".json");
    }

    private Path getTextPath(ManualTextProvider model) {
        ResourceLocation loc = model.getLocation();
        return generator.getOutputFolder().resolve("assets/" + loc.getNamespace() + "/manual/" + loc.getPath() + ".txt");
    }
}
