package com.igteam.immersivegeology.common.data.helper;

import com.google.gson.JsonObject;
import com.igteam.immersivegeology.core.lib.IGLib;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class CompatBlockModelProvider extends ModelProvider<CompatBlockModelProvider.CompatBlockModelBuilder> {
    public CompatBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, IGLib.MODID, "block", CompatBlockModelBuilder::new, existingFileHelper);
    }

    protected void registerModels()
    {

    }

    public @NotNull String getName() {
        return "Compatibility Block Models: " + this.modid;
    }

    public static class CompatBlockModelBuilder extends ModelBuilder<CompatBlockModelBuilder> {
        public CompatBlockModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
            super(outputLocation, existingFileHelper);
        }

        public void unsafeTexture(String key, String texture) {
            this.textures.put(key, texture);
        }
    }
}
