package igteam.immersive_geology.generators;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.item.IGGenericItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.materials.helper.IGRegistryProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class IGItemModelProvider extends ItemModelProvider {

    private Logger logger = ImmersiveGeology.getNewLogger();

    public IGItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, IGLib.MODID, existingFileHelper);
    }

    @NotNull
    @Override
    public String getName() {
        return "Item Models";
    }

    @Override
    protected void registerModels() {
        IGRegistryProvider.IG_ITEM_REGISTRY.values().forEach((i) -> {
            if(i instanceof IGGenericItem item) {
                generateGenericItem(item);
            }
        });
    }

    private void generateGenericItem(IGGenericItem item){
        withExistingParent(new ResourceLocation(IGLib.MODID, "item/" + item.getHolderKey()).getPath(),
                new ResourceLocation(IGLib.MODID, "item/base/" + item.getPattern().getName()));
    }
}
