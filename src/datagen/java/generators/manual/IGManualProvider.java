package generators.manual;

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
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.processing.IGProcessingStage;
import igteam.api.processing.helper.RecipeMethod;
import igteam.api.processing.methods.IGCraftingMethod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

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
        for(MaterialInterface<MaterialBaseMetal> metal : MetalEnum.values()){
            String material_name = metal.getName();
            String formalName = material_name.substring(0,1).toUpperCase() + material_name.substring(1).toLowerCase();

            ManualPageProvider provider = attemptPageCreation(material_name)
                    .startAnchor(material_name + "_display")
                    .setType(IGManualType.item_display)
                    .addListElements("items",
                            metal.getItem(ItemPattern.ingot).getRegistryName(),
                            metal.getItem(ItemPattern.nugget).getRegistryName(),
                            metal.getItem(ItemPattern.dust).getRegistryName())
                    .closeAnchor();

            ManualTextProvider textProvider = attemptTextCreation(material_name)
                    .setTitle(formalName, metal.instance().getRarity().name() + " metal");

            String sanitizedProcessing = "";
            StringBuilder metalSources = new StringBuilder("");


            metal.instance().getStages().stream().forEach((stage) -> {
                if(!stage.getMethods().isEmpty()) {
                    metalSources.append(stage.getStageName() + ":\n");
                    stage.getMethods().stream().forEach((method) -> {
                        method.clearRecipePath();
                        if(method.getGenericInput() != null) {
                            String dirtyTag = method.getGenericInput().toString();
                            String tag = dirtyTag.substring(dirtyTag.indexOf(":") + 1, dirtyTag.lastIndexOf("]")).replace("s/", " ").replace("/", " ");
                            IGApi.getNewLogger().info("Generic Input: " + tag);
                            metalSources.append("Process " + tag + " by <link;" + method.getRecipeType().getMachineName() + ";" + method.getRecipeType().name() + ">, \n");
                        }
                    });
                    metalSources.append("");
                }
            });
            StringBuilder sourceLink = new StringBuilder();
            if(metalSources.toString().length() > 0) {
                sanitizedProcessing = (metalSources.toString().substring(0, metalSources.toString().lastIndexOf(",")));
            }

            ArrayList<MaterialInterface<? extends MaterialBaseMineral>> sourceMinerals = metal.instance().getSourceMinerals();

            for (MaterialInterface<? extends MaterialBaseMineral> mineral : sourceMinerals) {
                sourceLink.append("<link;immersive_geology:" + mineral.getName() + ";" + mineral.getName() + ">, \n");
            }

            String sourceText = "";
            if(!sourceLink.toString().isEmpty()) {
                sourceText = "Metal can be processed from: " + (sourceLink.toString().substring(0, sourceLink.toString().lastIndexOf(",")) + "");
            }

            IGApi.getNewLogger().info(sanitizedProcessing);
            textProvider.attachPage(material_name + "_display", sourceText + "\n" + sanitizedProcessing);
        }

        for (MaterialInterface<MaterialBaseMineral> material : MineralEnum.values()){
            MaterialBaseMineral baseMaterial = material.instance();
            String material_name = baseMaterial.getName();
            String formalName = material_name.substring(0,1).toUpperCase() + material_name.substring(1).toLowerCase();

            ManualPageProvider provider = attemptPageCreation(material_name);

            if(baseMaterial.hasPattern(ItemPattern.ore_chunk) && baseMaterial.hasPattern(ItemPattern.dirty_crushed_ore)) {
                    provider.startAnchor(material_name + "_display")
                    .setType(IGManualType.item_display)
                    .addListElements("items",
                            StoneEnum.Stone.getItem(ItemPattern.ore_chunk, baseMaterial).getRegistryName(),
                            StoneEnum.Stone.getItem(ItemPattern.dirty_crushed_ore, baseMaterial).getRegistryName())
                    .closeAnchor();
            }
            String sanitizedMaterials = "";

            if(!baseMaterial.getSourceMaterials().isEmpty()) {
                StringBuilder materialSources = new StringBuilder("[");

                baseMaterial.getSourceMaterials().stream().forEach((smat) -> {
                    materialSources.append( "<link;" + IGApi.MODID + ":" + smat.getName() + ";" + smat.getName().replace("_", " ") + ">, ");
                });
                sanitizedMaterials = (materialSources.toString().substring(0, materialSources.toString().lastIndexOf(",")) + "]");
            }

            ManualTextProvider textProvider = attemptTextCreation(material_name)
                    .setTitle(formalName, baseMaterial.getRarity().name() + " mineral")
                    .attachPage(material_name + "_display",
                            formalName + " is found in the " + material.getDimension().name() + " between y layers " + ((IGOreConfig) material.getGenerationConfig()).minY.get() + " and " + ((IGOreConfig) material.getGenerationConfig()).maxY.get() +
                                    (sanitizedMaterials.isEmpty() ? "" : " and provides a source of " + sanitizedMaterials + ".") + "\nFurther information on how to process this Mineral can be found in the next few pages.");

            HashSet<ResourceLocation> crafting = new HashSet<>();
            HashSet<ResourceLocation> crushing = new HashSet<>();
            StringBuilder mineralMethods = new StringBuilder();

            for (IGProcessingStage stage : material.getStages()) {
                stage.getMethods().stream().forEach((m) -> {
                    m.clearRecipePath();

                    if(m.getRecipeType() == RecipeMethod.Crafting) {
                        IGCraftingMethod crft = (IGCraftingMethod) m;
                        if(crft.getRecipeGroup().equals("wash_dirty_ore"))
                            crafting.add(m.getLocation());
                    } else if(!stage.getMethods().isEmpty()) {
                        mineralMethods.append(stage.getStageName() + ":\n");
                        if(m.getGenericInput() != null) {
                            String dirtyTag = m.getGenericInput().toString();
                            String tag = dirtyTag.substring(dirtyTag.indexOf(":") + 1, dirtyTag.lastIndexOf("]")).replace("s/", " ").replace("/", " ");
                            IGApi.getNewLogger().info("Generic Input: " + tag);
                            mineralMethods.append("Process " + tag + " by <link;" + m.getRecipeType().getMachineName() + ";" + m.getRecipeType().name() + ">, \n");
                        }

                        mineralMethods.append("");
                    }
                });
            }

            provider.startAnchor(material_name + "_recipe")
                    .setType(IGManualType.crafting)
                    .addListElements("recipes", crafting.toArray(new ResourceLocation[crafting.size()]));

            textProvider.attachPage(material_name + "_recipe", "(Generic) Crafting Recipe");
            textProvider.attachPage(material_name + "_display", mineralMethods.toString());
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
