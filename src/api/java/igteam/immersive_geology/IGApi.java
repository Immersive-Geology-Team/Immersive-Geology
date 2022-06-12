package igteam.immersive_geology;

import blusunrize.immersiveengineering.api.*;
import blusunrize.immersiveengineering.common.IEContent;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.IRegistryDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

import java.awt.*;
import java.util.*;
import java.util.List;

public class IGApi {
    public final static String MODID = "immersive_geology";
    public static Logger getNewLogger()
    {
        return LogManager.getLogger(StackLocatorUtil.getCallerClass(2));
    }

    public static String getWrapFromSet(LinkedHashSet<MaterialBase> matSet){
        StringJoiner value = new StringJoiner(",");

        for (MaterialBase m : matSet) {
            value.add(m.getName());
        }
        return "[" + value.toString() + "]";
    }

    public static Color mixColors(Color... colors) {
        float ratio = 1f / ((float) colors.length);
        int r = 0, g = 0, b = 0, a = 0;
        for (Color color : colors) {
            r += color.getRed() * ratio;
            g += color.getGreen() * ratio;
            b += color.getBlue() * ratio;
            a += color.getAlpha() * ratio;
        }
        return new Color(r, g, b, a);
    }

    public static void init(){};
    public static Item grabIEItemFromRegistry(ItemPattern p, EnumMetals ieMetals){
        IGApi.getNewLogger().debug("DEBUG: Getting IE or MC Item From Registration");
        if(MaterialBase.isExistingPattern(p)){
            //MANUAL FILTH :( ~Muddykat
            switch(p){
                case ingot:
                    Item vanIngot = (ieMetals == EnumMetals.GOLD) ? Items.GOLD_INGOT : ((ieMetals == EnumMetals.IRON) ? Items.IRON_INGOT : null);
                    if(vanIngot != null){
                        return vanIngot;
                    }
                case nugget:
                    Item vanNugget = (ieMetals == EnumMetals.GOLD) ? Items.GOLD_NUGGET : ((ieMetals == EnumMetals.IRON) ? Items.IRON_NUGGET : null);
                    if(vanNugget != null){
                        return vanNugget;
                    }
            }

            try{
                return IEContent.registeredIEItems.stream().filter((item -> Objects.requireNonNull(item.getRegistryName()).getPath().equals(p.getName() + "_" + ieMetals.name().toLowerCase()))).findFirst().get();
            } catch(Exception exception) {
                getNewLogger().error(exception.getMessage());
            }

        }

//        if(MaterialBase.isExistingPattern(p)){
//            try{
//                ResourceLocation loc = new ResourceLocation((ieMetals.isVanillaMetal() && (p == ItemPattern.ingot || p == ItemPattern.nugget)) ? "minecraft" : Lib.MODID, p.getName().toLowerCase() + "_" + ieMetals.name().toLowerCase());
//                return ForgeRegistries.ITEMS.getValue(loc);
//            } catch (Exception ex){
//                getNewLogger().error(ex.getMessage());
//            }
//        }

        return null;
    }
}
