package igteam.api.veins;

import igteam.api.materials.data.mineral.MaterialBaseMineral;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class IGOreVein {
    private Map<TagKey<Item>, Float> veinContent;
    private ResourceLocation spawnDimension;
    private int weight;
    private float fail_chance;

    private String name;

    public IGOreVein(ResourceLocation spawnDimension) {
        this.spawnDimension = spawnDimension;
        this.weight = 0;
        this.fail_chance = 0.0f;
        this.veinContent = new HashMap<>();
    }

    public IGOreVein addMineral(TagKey<Item> mineral, int share) {
        this.veinContent.put(mineral, (float) share);
        return this;
    }

    public IGOreVein setVeinWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public void recalculateOutputShares() {
        float sharesSum = 0;
        for (Float t : veinContent.values()) {
            sharesSum += t;
        }

        if (sharesSum != 0) {
            Map<TagKey<Item>, Float> temp = new HashMap<>();
            for (TagKey<Item> mineral : veinContent.keySet()) {
                float share = Math.round((veinContent.get(mineral) / sharesSum) * 100) / 100f; //I wanna round chances
                temp.put(mineral, share);
            }
            veinContent = temp;
        }
    }

    public float getFailChance() {
        return this.fail_chance;
    }

    public IGOreVein setFailChance(float chance) {
        this.fail_chance = chance;
        return this;
    }

    public String getVeinName() {
        return this.name;
    }

    public IGOreVein setVeinName(String name) {
        this.name = name;
        return this;
    }

    public int getWeight() {
        return this.weight;
    }

    public Map<TagKey<Item>, Float> getVeinContent() {
        return this.veinContent;
    }

    public ResourceLocation getSpawnDimension() {
        return this.spawnDimension;
    }
}
