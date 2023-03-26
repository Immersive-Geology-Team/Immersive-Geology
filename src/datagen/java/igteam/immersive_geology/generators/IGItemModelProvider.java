package igteam.immersive_geology.generators;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.common.item.IGGenericItem;
import com.igteam.immersive_geology.common.item.helper.IGFlagItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.material.GeologyMaterial;
import com.igteam.immersive_geology.core.material.helper.MaterialTexture;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Level;

import java.util.List;

public class IGItemModelProvider extends ItemModelProvider {
    public IGItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, IGLib.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        List<Item> itemList = IGRegistrationHolder.supplyDeferredItems().get();

        for (Item item : itemList) {
            if(item instanceof IGGenericItem i){
                generateGenericItem(i);
            }
            if(item instanceof IGGenericBlockItem i){
                generateGenericBlockItem(i);
            }
        }

    }

    private void generateGenericItem(IGFlagItem item){
        String itemLocation = new ResourceLocation(IGLib.MODID, "item/" + item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.base))).getPath();

        try {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_base_item");
            withExistingParent(itemLocation, parentLocation).texture("layer0", item.getMaterial(MaterialTexture.base).getTextureLocation(item.getFlag()));

            if(item.getMaterial(MaterialTexture.overlay) != null) {
                getBuilder(itemLocation).texture("layer1", item.getMaterial(MaterialTexture.overlay).getTextureLocation(item.getFlag()));
            }

        } catch (Exception ex) {
            ImmersiveGeology.getNewLogger().log(Level.ERROR, "Attempted to generate a texture for the item type '" + item.getFlag().getName() + "' with material '" + item.getMaterial(MaterialTexture.base).getName() + "'");
        }
    }

    private void generateGenericBlockItem(IGFlagItem item){
        if(item instanceof IGGenericItem) {
            ImmersiveGeology.getNewLogger().log(Level.ERROR, "Wrong input parse in generateGenericBlockItem, used normal item as input see: " + item.getFlag().getName() + " and " + item.getMaterial(MaterialTexture.base).getName());
            return;
        }

        String itemLocation = new ResourceLocation(IGLib.MODID, "item/" + item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.base))).getPath();
        String overlayExtra = item.getMaterial(MaterialTexture.overlay) != null ? "_" + item.getMaterial(MaterialTexture.overlay).getName() : "";
        ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "block/" + item.getFlag().getName() + "/" + item.getFlag().getName() + "_" + item.getMaterial(MaterialTexture.base).getName() + overlayExtra);

        withExistingParent(itemLocation, parentLocation);
    }

}
