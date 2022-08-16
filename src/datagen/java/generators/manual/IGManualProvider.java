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
import org.apache.logging.log4j.Logger;

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

            ManualPageProvider provider = attemptPageCreation(mineral_name);

            provider.startAnchor(mineral_name + "_introduction").setType(IGManualType.text).closeAnchor();

            ManualTextProvider mineralIntroPage = attemptTextCreation(mineral_name).setTitle(title_name, mineral.instance().getRarity().name());

            String intro_text = "Hello, I'm an introductionary page";

            mineralIntroPage.attachPage(mineral_name + "_introduction", intro_text);

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
