package igteam.immersive_geology;

import igteam.immersive_geology.materials.data.MaterialBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

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

        getNewLogger().info("Creating Wrap for Tags: " + "[" + value.toString() + "]");
        return "[" + value.toString() + "]";
    }

}
