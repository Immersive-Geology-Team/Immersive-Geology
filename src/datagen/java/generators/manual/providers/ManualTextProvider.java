package generators.manual.providers;

import com.google.gson.JsonObject;
import generators.manual.helper.IGManualType;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

public class ManualTextProvider {
    protected ResourceLocation location;
    protected String name, title, subtitle;
    protected int priority;

    protected final StringBuilder finalString;
    protected LinkedHashMap<String, String> boundPages = new LinkedHashMap<>();

    public ManualTextProvider(ResourceLocation location) {
        this.location = location;
        this.name = location.getPath().toLowerCase();

        this.finalString = new StringBuilder();
    }

    public ManualTextProvider attachPage(String anchor, String text) {
        boundPages.put(anchor, text);
        return this;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public String getResult() {
        finalString.append(title + "\n");

        finalString.append(subtitle + "\n");

        List<String> keyList = new ArrayList<>(boundPages.keySet());

        for (String key : keyList) {
            String line = "<&" + key + "> " + boundPages.get(key) + "\n";
            finalString.append(line);
        }

        return finalString.toString();
    }

    public ManualTextProvider setTitle(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        return this;
    }
}
