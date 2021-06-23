package com.igteam.immersive_geology.core.data.generators.recipe;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.CrusherRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.MetalPressRecipeBuilder;
import blusunrize.immersiveengineering.common.items.IEItems;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.util.IGRegistryGrabber;
import com.igteam.immersive_geology.core.data.generators.helpers.IGTags;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistration;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import com.mojang.authlib.UserType;
import com.sun.swing.internal.plaf.metal.resources.metal;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.nio.file.Path;

import static com.igteam.immersive_geology.core.data.generators.helpers.IGTags.createItemWrapper;

public class IGRecipeProvider extends RecipeProvider {
    private final HashMap<String, Integer> PATH_COUNT = new HashMap<>();
    private final Path ADV_ROOT;

    private final Logger log = ImmersiveGeology.getNewLogger();

    public IGRecipeProvider(DataGenerator gen) {
        super(gen);
        ADV_ROOT = gen.getOutputFolder().resolve("data/minecraft/advancements/recipes/root.json");
    }

    @Nonnull
    @Override
    public String getName() {
        return super.getName() + ": " + IGLib.MODID;
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        CrusherRecipeBuilder crusherBuilder;

        for(MaterialEnum material : MaterialEnum.values()) {
            IGTags.MaterialTags tags = IGTags.getTagsFor(material);

            Item nugget = MaterialUseType.NUGGET.getItem(material);
            Item ingot =  MaterialUseType.INGOT.getItem(material);
            Item dust = MaterialUseType.DUST.getItem(material);

            if(material.getMaterial().hasSubtype(MaterialUseType.NUGGET) && material.getMaterial().hasSubtype(MaterialUseType.INGOT)){
                add3x3Conversion(ingot, nugget, tags.nugget, consumer);
                log.info("Generated Ingot/Nugget Recipe for: " + material.getMaterial().getName());

                Block metal_block = MaterialUseType.STORAGE_BLOCK.getBlock(material);
                if(metal_block != null) {
                    add3x3Conversion(metal_block, ingot, tags.ingot, consumer);
                    log.info("Generate Storage Block recipe for: " + material.getMaterial().getName());
                }

                if(!material.getMaterial().preExists()) {
                    crusherBuilder = CrusherRecipeBuilder.builder(tags.dust, 1);
                    crusherBuilder.addCondition(getTagCondition(tags.dust)).addCondition(getTagCondition(tags.ingot));
                    crusherBuilder.addInput(tags.ingot)
                            .setEnergy(3000)
                            .build(consumer, toRL("crusher/ingot_" + material.getMaterial().getName()));
                }
            }
            if (material.getMaterial().hasSubtype(MaterialUseType.PLATE)) {
                MetalPressRecipeBuilder.builder(IEItems.Molds.moldPlate, tags.plate, 1).addInput(tags.ingot)
                        .setEnergy(2400).build(consumer, toRL("metalpress/plate_"+material.getMaterial().getName()));
            }

            if (material.getMaterial().hasSubtype(MaterialUseType.ROD)) {
                MetalPressRecipeBuilder.builder(IEItems.Molds.moldRod, tags.rod, 1).addInput(tags.ingot)
                        .setEnergy(2400).build(consumer, toRL("metalpress/rod_"+material.getMaterial().getName()));
            }


            if (material.getMaterial().hasSubtype(MaterialUseType.GEAR)) {
                MetalPressRecipeBuilder.builder(IEItems.Molds.moldGear, tags.gear, 1)
                        .addInput(new IngredientWithSize(tags.ingot,4))
                        .setEnergy(2400).build(consumer, toRL("metalpress/gear_"+material.getMaterial().getName()));
            }
            if (material.getMaterial().hasSubtype(MaterialUseType.WIRE)) {
                MetalPressRecipeBuilder.builder(IEItems.Molds.moldWire, tags.wire, 2).addInput(tags.ingot)
                        .setEnergy(2400).build(consumer, toRL("metalpress/wire_"+material.getMaterial().getName()));

            }
            for (MaterialEnum stone_base : MaterialEnum.stoneValues()) {
                if (material.getMaterial().hasSubtype(MaterialUseType.ORE_CHUNK)) {
                    Item ore_chunk = MaterialUseType.ORE_CHUNK.getItem(stone_base, material);
                    MaterialEnum processed_material = material.getMaterial().getProcessedType();

                    if(material.getMaterial().isNativeMetal) // means that it's a native metal chunk e.g gold_chunk, it's material is gold, so it's processed into gold.
                    {
                        processed_material = material;
                    }

                    if(processed_material != null) {
                        Item ore_ingot = MaterialUseType.INGOT.getItem(processed_material);

                        if (ore_ingot != null && ore_chunk != null) {
                            addBlastingRecipe(ore_chunk, ore_ingot, 0, 150, consumer);
                            log.info("Generated Blasting Recipe for: " + ore_chunk.getRegistryName());
                        }
                    }

                    MaterialEnum secondary_material = material.getMaterial().getSecondaryType();

                    crusherBuilder = CrusherRecipeBuilder.builder(tags.crushed_ore, 2);
                    if(secondary_material != null){
                        Item secondary_out = MaterialUseType.DUST.getItem(secondary_material);
                        if(secondary_out != null) {
                            crusherBuilder.addSecondary(secondary_out, 0.33f);
                        }
                    }
                    crusherBuilder.addInput(ore_chunk).setEnergy(6000).build(consumer, toRL("crusher/ore_"+material.getMaterial().getName()));

                }

            }


        }



        getSubRecipeProviders().forEach(subRecipeProvider -> subRecipeProvider.addRecipes(consumer));
    }


    private void add3x3Conversion(IItemProvider bigItem, IItemProvider smallItem, ITag.INamedTag<Item> smallTag, Consumer<IFinishedRecipe> out)
    {
        ShapedRecipeBuilder.shapedRecipe(bigItem)
                .key('s', smallTag)
                .key('i', smallItem)
                .patternLine("sss")
                .patternLine("sis")
                .patternLine("sss")
                .addCriterion("has_"+toPath(smallItem), hasItem(smallItem))
                .build(out, toRL(toPath(smallItem)+"_to_")+toPath(bigItem));
        ShapelessRecipeBuilder.shapelessRecipe(smallItem, 9)
                .addIngredient(bigItem)
                .addCriterion("has_"+toPath(bigItem), hasItem(smallItem))
                .build(out, toRL(toPath(bigItem)+"_to_"+toPath(smallItem)));
    }

    private final int standardSmeltingTime = 200;
    private final int blastDivider = 2;

    public static ICondition getTagCondition(ITag.INamedTag<?> tag)
    {
        return new NotCondition(new TagEmptyCondition(tag.getName()));
    }

    public static ICondition getTagCondition(ResourceLocation tag)
    {
        return getTagCondition(createItemWrapper(tag));
    }

    private void addStandardSmeltingBlastingRecipe(IItemProvider input, IItemProvider output, float xp, Consumer<IFinishedRecipe> out)
    {
        addStandardSmeltingBlastingRecipe(input, output, xp, out, "");
    }

    private void addStandardSmeltingBlastingRecipe(IItemProvider input, IItemProvider output, float xp, Consumer<IFinishedRecipe> out, String extraPostfix)
    {
        addStandardSmeltingBlastingRecipe(input, output, xp, standardSmeltingTime, out, extraPostfix, false);
    }

    private void addStandardSmeltingBlastingRecipe(IItemProvider input, IItemProvider output, float xp, int smeltingTime, Consumer<IFinishedRecipe> out, String extraPostfix, boolean smeltPostfix)
    {
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, xp, smeltingTime).addCriterion("has_"+toPath(input), hasItem(input)).build(out, toRL(toPath(output)+extraPostfix+(smeltPostfix?"_from_smelting": "")));
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(input), output, xp, smeltingTime/blastDivider).addCriterion("has_"+toPath(input), hasItem(input)).build(out, toRL(toPath(output)+extraPostfix+"_from_blasting"));
    }

    private void addBlastingRecipe(IItemProvider input, IItemProvider output, float xp, int smeltingTime, Consumer<IFinishedRecipe> out){
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(input), output, xp, smeltingTime).addCriterion("has_"+toPath(input), hasItem(input)).build(out, toRL(toPath(output)+"_blasting"));
    }

    private String toPath(IItemProvider src)
    {
        return src.asItem().getRegistryName().getPath();
    }

    /**
     * Gets all the sub/offloaded recipe providers that this recipe provider has.
     *
     * @implNote This is only called once per provider so there is no need to bother caching the list that this returns
     */
    protected List<ISubRecipeProvider> getSubRecipeProviders() {
        return Collections.emptyList();
    }

    private ResourceLocation toRL(String s)
    {
        if(!s.contains("/"))
            s = "crafting/"+s;
        if(PATH_COUNT.containsKey(s))
        {
            int count = PATH_COUNT.get(s)+1;
            PATH_COUNT.put(s, count);
            return new ResourceLocation(IGLib.MODID, s+count);
        }
        PATH_COUNT.put(s, 1);
        return new ResourceLocation(IGLib.MODID, s);
    }
}
