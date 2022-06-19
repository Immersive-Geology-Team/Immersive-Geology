package generators.manual;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import generators.manual.helper.IGManualType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Items;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class IGManualProvider implements IDataProvider {
    protected static final ExistingFileHelper.ResourceType PAGE = new ExistingFileHelper.ResourceType(ResourcePackType.CLIENT_RESOURCES, ".json", "page");
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final DataGenerator generator;
    protected final Function<ResourceLocation, ManualPageProvider> factory;
    @VisibleForTesting
    public final Map<ResourceLocation, ManualPageProvider> generatedPages = new HashMap<>();
    @VisibleForTesting
    public final ExistingFileHelper existingFileHelper;

    protected final String folder;
    protected final String modid;
    public IGManualProvider(DataGenerator generator, ExistingFileHelper existingFileHelper, String modid) {
        Preconditions.checkNotNull(generator);
        this.generator = generator;

        Preconditions.checkNotNull(existingFileHelper);
        this.existingFileHelper = existingFileHelper;

        this.folder = ""; //Don't need this at the moment.
        this.modid = modid;

        this.factory = ManualPageProvider::new;
    }

    public void registerPages(){
        attemptPageCreation("ore_test").setType(IGManualType.item_display)
                .addListElement("item", Objects.requireNonNull(Items.IRON_BLOCK.getRegistryName()).toString());
    }

    private ManualPageProvider attemptPageCreation(String path) {
        Preconditions.checkNotNull(path, "Path must not be null");
        ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modid, path));
        this.existingFileHelper.trackGenerated(outputLoc, PAGE);
        return generatedPages.computeIfAbsent(outputLoc, factory);
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
            Path target = getPath(model);
            try {
                IDataProvider.save(GSON, cache, model.toJson(), target);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path getPath(ManualPageProvider model) {
        ResourceLocation loc = model.getLocation();
        return generator.getOutputFolder().resolve("assets/" + loc.getNamespace() + "/manual/" + loc.getPath() + ".json");
    }
}
