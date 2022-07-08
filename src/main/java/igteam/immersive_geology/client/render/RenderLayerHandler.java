package igteam.immersive_geology.client.render;

import igteam.immersive_geology.common.fluid.IGFluid;
import igteam.api.main.IGMultiblockProvider;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.Map;

public class RenderLayerHandler {
    private static final Map<Block, RenderTypeSkeleton> mapping = new HashMap<>();
    private static final Map<Block, Block> inheritances = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    private static Map<RenderTypeSkeleton, RenderType> renderTypes;

    public static void setRenderType(Block block, RenderTypeSkeleton skeleton) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            setRenderTypeClient(block, skeleton);
        });
    }

    public static void setInherited(Block block, Block parent) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            setInheritedClient(block, parent);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void init(FMLClientSetupEvent event) {
        for(Block b : inheritances.keySet()) {
            Block inherit = inheritances.get(b);
            if(mapping.containsKey(inherit))
                mapping.put(b, mapping.get(inherit));
        }

        for(Block b : mapping.keySet()) {
            RenderTypeLookup.setRenderLayer(b, renderTypes.get(mapping.get(b)));
        }

        for(IGFluid fluid : IGFluid.IG_FLUIDS){
            if(!fluid.isSolidFluid()) {
                RenderTypeLookup.setRenderLayer(fluid, RenderType.getTranslucent());
                RenderTypeLookup.setRenderLayer(fluid.getFlowingFluid(), RenderType.getTranslucent());
            }
        }

        //This is to tell minecraft that we want to render some of the texture as a cutout.
        RenderTypeLookup.setRenderLayer(IGMultiblockProvider.chemicalvat, RenderType.getCutoutMipped());

        RenderTypeLookup.setRenderLayer(IGMultiblockProvider.bloomery, RenderType.getCutout());

        inheritances.clear();
        mapping.clear();
    }

    @OnlyIn(Dist.CLIENT)
    private static void setRenderTypeClient(Block block, RenderTypeSkeleton skeleton) {
        resolveRenderTypes();
        mapping.put(block, skeleton);
    }

    @OnlyIn(Dist.CLIENT)
    private static void setInheritedClient(Block block, Block parent) {
        resolveRenderTypes();
        inheritances.put(block, parent);
    }

    @OnlyIn(Dist.CLIENT)
    private static void resolveRenderTypes() {
        if(renderTypes == null) {
            renderTypes = new HashMap<>();

            renderTypes.put(RenderTypeSkeleton.SOLID, RenderType.getSolid());
            renderTypes.put(RenderTypeSkeleton.CUTOUT, RenderType.getCutout());
            renderTypes.put(RenderTypeSkeleton.CUTOUT_MIPPED, RenderType.getCutoutMipped());
            renderTypes.put(RenderTypeSkeleton.TRANSLUCENT, RenderType.getTranslucent());
        }
    }

    public enum RenderTypeSkeleton {
        SOLID,
        CUTOUT,
        CUTOUT_MIPPED,
        TRANSLUCENT;
    }
}
