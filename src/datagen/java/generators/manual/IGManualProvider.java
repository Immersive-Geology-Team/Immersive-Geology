package generators.manual;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import generators.manual.helper.IGManualType;
import generators.manual.providers.ManualPageProvider;
import generators.manual.providers.ManualTextProvider;
import igteam.immersive_geology.materials.MineralEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.processing.IGProcessingStage;
import igteam.immersive_geology.processing.helper.RecipeMethod;
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
        for (MaterialInterface<MaterialBaseMineral> material : MineralEnum.values()){
            MaterialBaseMineral baseMaterial = material.get();
            String material_name = baseMaterial.getName();
            String formalName = material_name.substring(0,1).toUpperCase() + material_name.substring(1).toLowerCase();

            ManualPageProvider provider = attemptPageCreation(material_name)
                    .startAnchor(material_name + "_display")
                        .setType(IGManualType.item_display)
                        .addListElements("items",
                                StoneEnum.Stone.getItem(ItemPattern.ore_chunk, baseMaterial).getRegistryName(),
                                StoneEnum.Stone.getItem(ItemPattern.dirty_crushed_ore, baseMaterial).getRegistryName())
                    .closeAnchor();

            ManualTextProvider textProvider = attemptTextCreation(material_name)
                    .setTitle(formalName, baseMaterial.getRarity().name() + " mineral")
                    .attachPage(material_name + "_display",
                            formalName + " is found between y layers: " + material.getGenerationConfig().minY.get() + " and " + material.getGenerationConfig().maxY.get() +
                            " and a source of " + baseMaterial.getSourceMetals() + ". Further information on how to process this Mineral can be found in the next few pages.");

            List<ResourceLocation> crafting = new ArrayList<>();
            List<ResourceLocation> crushing = new ArrayList<>();

            for (IGProcessingStage stage : material.getStages()) {
                stage.getMethods().stream().forEach((m) -> {
                    m.clearRecipePath();

                    if(m.getRecipeType() == RecipeMethod.Crafting) {
                        crafting.add(m.getLocation());
                    }

                    if(m.getRecipeType() == RecipeMethod.Crushing) {
                        crushing.add(m.getLocation());
                    }
                });
            }

            provider.startAnchor(material_name + "_recipe")
                    .setType(IGManualType.crafting)
                    .addListElements("recipes", crafting.toArray(new ResourceLocation[crafting.size()]));

            textProvider.attachPage(material_name + "_recipe", "(Generic) Crafting Recipe");
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
