package igteam.immersive_geology.generators;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.IGGenericBlock;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.common.item.IGGenericItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.materials.helper.IGRegistryProvider;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Level;
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
                ItemPattern pattern = item.getPattern();
                if (pattern != ItemPattern.block_item) {
                    generateGenericItem(item);
                }
            }
            if(i instanceof IGGenericBlockItem item){
                IGGenericBlock block = (IGGenericBlock) item.getBlock();
                BlockPattern blockPattern = block.getPattern();
                switch(blockPattern) {
                    case ore -> {
                        generateOreItemBlock(block, item);
                    }
                    default -> generateGenericItemBlock(block, item);
                }
            }
        });
    }

    private void generateGenericItem(IGGenericItem item){
        String item_loc = new ResourceLocation(IGLib.MODID, "item/" + item.getHolderKey()).getPath();
        withExistingParent(item_loc,
                new ResourceLocation(IGLib.MODID, "item/base/ig_base_item"))
                .texture("layer0", item.getMaterial(MaterialTexture.base).getTextureLocation(item.getPattern()));

        if(item.getMaterial(MaterialTexture.overlay) != null) {
            getBuilder(item_loc).texture("layer1", item.getMaterial(MaterialTexture.overlay).getTextureLocation(item.getPattern()));
        }
    }

    private void generateGenericItemBlock(IGGenericBlock block, IGGenericBlockItem item){
        withExistingParent(new ResourceLocation(IGLib.MODID, "item/" + item.getHolderKey() + "_" + block.getPattern().getName()).getPath(),
                new ResourceLocation(IGLib.MODID, "block/" + block.getPattern().getName() + "_" + block.getMaterial(MaterialTexture.base).getName() +
                        (block.getMaterial(MaterialTexture.overlay) != null ? "_" + block.getMaterial(MaterialTexture.overlay).getName() : "")));
    }

    private void generateOreItemBlock(IGGenericBlock block, IGGenericBlockItem item) {
        String item_loc = new ResourceLocation(IGLib.MODID, "item/" + item.getHolderKey() + "_" + block.getPattern().getName()).getPath();
        withExistingParent(item_loc, new ResourceLocation(IGLib.MODID, "block/base/" + block.getPattern().getName()));
        getBuilder(item_loc).texture("base", block.getMaterial(MaterialTexture.base).getTextureLocation(block.getPattern()));
        getBuilder(item_loc).texture("ore", block.getMaterial(MaterialTexture.overlay).getTextureLocation(block.getPattern()));
        getBuilder(item_loc).element().allFaces(((direction, faceBuilder) -> faceBuilder.texture("#base").tintindex(0).uvs(0, 0, 16, 16)));
        getBuilder(item_loc).element().allFaces(((direction, faceBuilder) -> faceBuilder.texture("#ore").tintindex(1).uvs(0, 0, 16, 16)));
    }
}