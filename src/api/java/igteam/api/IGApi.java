package igteam.api;

import blusunrize.immersiveengineering.api.EnumMetals;
import igteam.api.materials.data.MaterialBase;
import igteam.api.materials.pattern.ItemFamily;
import igteam.api.materials.pattern.MaterialPattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.StringJoiner;

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
        return "[" + value + "]";
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

    public static void init(){}

    public static Item grabIEItemFromRegistry(MaterialPattern pattern, EnumMetals ieMetals) {
        if (pattern instanceof ItemFamily) {
            ItemFamily p = (ItemFamily) pattern;
            IGApi.getNewLogger().debug("DEBUG: Getting IE or MC Item From Registration");
            if (MaterialBase.isExistingPattern(p)) {
                //MANUAL FILTH :( ~Muddykat
                switch (p) {
                    case ingot:
                        Item vanIngot = (ieMetals == EnumMetals.GOLD) ? Items.GOLD_INGOT : ((ieMetals == EnumMetals.IRON) ? Items.IRON_INGOT : null);
                        if (vanIngot != null) {
                            return vanIngot;
                        }
                    case nugget:
                        Item vanNugget = (ieMetals == EnumMetals.GOLD) ? Items.GOLD_NUGGET : ((ieMetals == EnumMetals.IRON) ? Items.IRON_NUGGET : null);
                        if (vanNugget != null) {
                            return vanNugget;
                        }
                }

                try {
                    if(DatagenModLoader.isRunningDataGen()){
                        //Run Hacky bad stuff in dev enviro to get the IE stuff
                    } else {
                        // When not in datagen we can run the good stuff!
                        return ForgeRegistries.ITEMS.getValue(new ResourceLocation("immersive_engineering",p.getName() + "_" + ieMetals.name().toLowerCase()));
                    }
                } catch (Exception exception) {
                    getNewLogger().error(exception.getMessage());
                }
            }
        }
        return null;
    }
}
