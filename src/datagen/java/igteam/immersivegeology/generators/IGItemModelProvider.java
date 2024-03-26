package igteam.immersivegeology.generators;

import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.common.item.IGGenericBlockItem;
import com.igteam.immersivegeology.common.item.IGGenericBucketItem;
import com.igteam.immersivegeology.common.item.IGGenericItem;
import com.igteam.immersivegeology.common.item.helper.IGFlagItem;
import com.igteam.immersivegeology.core.lib.IGLib;
import com.igteam.immersivegeology.core.material.helper.material.MaterialTexture;
import com.igteam.immersivegeology.core.registration.IGRegistrationHolder;
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
            if(item instanceof IGGenericBucketItem i){
                generateGenericBucketItem(i);
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
            ImmersiveGeology.getNewLogger().log(Level.ERROR, ex.getMessage());
        }
    }

    private void generateGenericBucketItem(IGFlagItem item){
        String itemLocation = new ResourceLocation(IGLib.MODID, "item/" + item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.base))).getPath();

        try {
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "item/base/ig_base_item");

            // TODO implement a better version of this, that supports 'colored' variants of the item textures, using the item flag system
            withExistingParent(itemLocation, parentLocation).texture("layer0", new ResourceLocation(IGLib.MODID, "item/greyscale/fluid/bucket_base"));
            getBuilder(itemLocation).texture("layer1", new ResourceLocation(IGLib.MODID, "item/greyscale/fluid/bucket_fluid"));
        } catch (Exception ex) {
            ImmersiveGeology.getNewLogger().log(Level.ERROR, "Attempted to generate a texture for the item type '" + item.getFlag().getName() + "' with material '" + item.getMaterial(MaterialTexture.base).getName() + "'");
            ImmersiveGeology.getNewLogger().log(Level.ERROR, ex.getMessage());
        }
    }

    private void generateGenericBlockItem(IGFlagItem item){
        if(item instanceof IGGenericBlockItem blockItem) {
            boolean complexItem = blockItem.getMaterials().size() > 1;

            String itemLocation = new ResourceLocation(IGLib.MODID, "item/" + (complexItem ?  item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.overlay), item.getMaterial(MaterialTexture.base)) : item.getFlag().getRegistryKey(item.getMaterial(MaterialTexture.base)))).getPath();
            String overlayExtra = item.getMaterial(MaterialTexture.overlay) != null ? "_" + item.getMaterial(MaterialTexture.overlay).getName() : "";
            ResourceLocation parentLocation = new ResourceLocation(IGLib.MODID, "block/" + item.getFlag().getName() + "/" + item.getFlag().getName() + overlayExtra + "_" + item.getMaterial(MaterialTexture.base).getName());

            withExistingParent(itemLocation, parentLocation);
            return;
        }

        ImmersiveGeology.getNewLogger().log(Level.ERROR, "Wrong input parse in generateGenericBlockItem, used normal item as input see: " + item.getFlag().getName() + " and " + item.getMaterial(MaterialTexture.base).getName());
    }

}
