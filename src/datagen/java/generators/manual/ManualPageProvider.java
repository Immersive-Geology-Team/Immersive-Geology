package generators.manual;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import generators.manual.helper.IGManualType;
import net.minecraft.item.Items;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.*;

public class ManualPageProvider {
    protected ResourceLocation location;
    protected IGManualType page_type;
    protected JsonObject root;
    public ManualPageProvider(ResourceLocation location) {
        this.location = location;
        root = new JsonObject();
    }

    public ResourceLocation getLocation() {
        assertExistence();
        return location;
    }

    public JsonElement toJson() {
        return root;
    }

    public void assertExistence() {
        Preconditions.checkState(exists(), "Page at %s does not exist", location);
    }

    protected boolean exists() {
        return true;
    }

    public ManualPageProvider setType(IGManualType type) {
        this.page_type = type;
        root.addProperty("type", type.name().toLowerCase());
        return this;
    }

    public ManualPageProvider addListElement(String key, String value) {
        JsonArray itemList = new JsonArray();
        itemList.add(value);
        root.add(key, itemList);
        return this;
    }
}
