package generators.recipe;


import blusunrize.immersiveengineering.api.EnumMetals;
import blusunrize.immersiveengineering.api.IETags;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.crafting.builders.ArcFurnaceRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.BlastFurnaceRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.CrusherRecipeBuilder;
import blusunrize.immersiveengineering.api.crafting.builders.MetalPressRecipeBuilder;
import blusunrize.immersiveengineering.common.items.IEItems;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.crafting.recipes.builders.*;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.fluid.FluidEnum;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.IGProcessingMethod;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.*;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialMetalBase;
import com.igteam.immersive_geology.api.materials.material_data.fluids.slurry.MaterialSlurryWrapper;
import com.igteam.immersive_geology.api.tags.IGTags;
import com.igteam.immersive_geology.common.fluid.IGFluid;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.igteam.immersive_geology.api.tags.IGTags.createItemWrapper;

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

        ShapedRecipeBuilder.shapedRecipe(IGRegistrationHolder.Blocks.electronicEngineering, 4)
                .patternLine("igi")
                .patternLine("gcg")
                .patternLine("igi")
                .key('i', IETags.getItemTag(IETags.getTagsFor(EnumMetals.STEEL).sheetmetal))
                .key('g', IEItems.Ingredients.componentSteel)
                .key('c', IEItems.Ingredients.circuitBoard)
                .addCriterion("has_steel_sheetmetal", hasItem(IETags.getItemTag(IETags.getTagsFor(EnumMetals.STEEL).sheetmetal)))
                .addCriterion("has_steel_component", hasItem(IEItems.Ingredients.componentSteel))
                .addCriterion("has_circuit_board", hasItem(IEItems.Ingredients.circuitBoard))
                .build(consumer, toRL("crafting/" + IEItems.Ingredients.circuitBoard.getItem().getRegistryName().getPath().toLowerCase()));

        for (MaterialEnum wrapper : MaterialEnum.values()) {
            Material material = wrapper.getMaterial();

            IGTags.MaterialTags tags = IGTags.getTagsFor(material);

            Item nugget = MaterialUseType.NUGGET.getItem(material);
            Item ingot = MaterialUseType.INGOT.getItem(material);
            Item dust = MaterialUseType.DUST.getItem(material);
            Item tiny_dust = MaterialUseType.TINY_DUST.getItem(material);
            Item clay = MaterialUseType.CLAY.getItem(material);
            Item brick = MaterialUseType.BRICK.getItem(material);
            Item bricks = MaterialUseType.BRICKS.getItem(material);
            Item rock = MaterialUseType.ROCK_BIT.getItem(material);
            Item cobble = MaterialUseType.COBBLESTONE.getItem(material);

            if(material.hasSubtype(MaterialUseType.CLAY) && material.hasSubtype(MaterialUseType.BRICK)) {
                addSmeltingRecipe(clay, brick, 0.15f, 300, consumer);
                addCampfireRecipe(clay, brick, 0.15f, 600, consumer);
                log.debug("Generated Clay/Brick Recipe for: " + material.getName());
            }

            if(material.hasSubtype(MaterialUseType.BRICK) && material.hasSubtype(MaterialUseType.BRICKS)) {
                add2x2Combine(bricks, brick, tags.brick, consumer);
                log.debug("Generated Brick/Bricks Dust Recipe for: " + material.getName());
            }

            if(material.hasSubtype(MaterialUseType.DUST) && material.hasSubtype(MaterialUseType.TINY_DUST)) {
                add3x3Conversion(dust, tiny_dust, tags.tiny_dust, consumer);
                log.debug("Generated Dust/Tiny Dust Recipe for: " + material.getName());
            }

            if (material.hasSubtype(MaterialUseType.NUGGET) && material.hasSubtype(MaterialUseType.INGOT)) {
                add3x3Conversion(ingot, nugget, tags.nugget, consumer);
                log.debug("Generated Ingot/Nugget Recipe for: " + material.getName());

                Block metal_block = MaterialUseType.STORAGE_BLOCK.getBlock(material);
                if (metal_block != null) {
                    add3x3Conversion(metal_block, ingot, tags.ingot, consumer);
                    log.debug("Generate Storage Block recipe for: " + material.getName());
                }

                if (!material.preExists()) {
                    crusherBuilder = CrusherRecipeBuilder.builder(tags.dust, 1);
                    crusherBuilder.addCondition(getTagCondition(tags.dust)).addCondition(getTagCondition(tags.ingot));
                    crusherBuilder.addInput(tags.ingot)
                            .setEnergy(3000)
                            .build(consumer, toRL("crusher/ingot_" + material.getName()));
                }
            }

            if (material.hasSubtype(MaterialUseType.PLATE)) {
                MetalPressRecipeBuilder.builder(IEItems.Molds.moldPlate, tags.plate, 1).addInput(tags.ingot)
                        .setEnergy(2400).build(consumer, toRL("metalpress/plate_" + material.getName()));
                if(material.hasSubtype(MaterialUseType.SHEETMETAL)) {
                    Item sheetmetal = MaterialUseType.SHEETMETAL.getBlock(material).asItem();
                    ShapedRecipeBuilder.shapedRecipe(sheetmetal)
                            .key('s', tags.plate)
                            .patternLine(" s ")
                            .patternLine("s s")
                            .patternLine(" s ")
                            .addCriterion("has_"+toPath(MaterialUseType.PLATE.getItem(material)), hasItem(tags.plate))
                            .build(consumer, toRL(toPath(sheetmetal)));
                }
            }

            if (material.hasSubtype(MaterialUseType.ROD)) {
                MetalPressRecipeBuilder.builder(IEItems.Molds.moldRod, tags.rod, 1).addInput(tags.ingot)
                        .setEnergy(2400).build(consumer, toRL("metalpress/rod_" + material.getName()));
            }

            if (material.hasSubtype(MaterialUseType.GEAR)) {
                MetalPressRecipeBuilder.builder(IEItems.Molds.moldGear, tags.gear, 1)
                        .addInput(new IngredientWithSize(tags.ingot, 4))
                        .setEnergy(2400).build(consumer, toRL("metalpress/gear_" + material.getName()));
            }

            if (material.hasSubtype(MaterialUseType.WIRE)) {
                MetalPressRecipeBuilder.builder(IEItems.Molds.moldWire, tags.wire, 2).addInput(tags.ingot)
                        .setEnergy(2400).build(consumer, toRL("metalpress/wire_" + material.getName()));

            }

            for (MaterialEnum stone_base : MaterialEnum.stoneValues()) {
                if(material.hasSubtype(MaterialUseType.ROCK_BIT) && material.hasSubtype(MaterialUseType.COBBLESTONE))
                {
                    if(material == MaterialEnum.Vanilla.getMaterial()) add2x2Combine(Blocks.COBBLESTONE, rock, consumer);
                    else add2x2Combine(cobble, rock, consumer);
                }
                if(material.hasSubtype(MaterialUseType.COBBLESTONE) && material.hasSubtype(MaterialUseType.STONE))
                {
                    if(material != MaterialEnum.Vanilla.getMaterial()) addSmeltingRecipe(cobble, MaterialUseType.STONE.getBlock(material).asItem(), 0.15f, 200, consumer);
                }
                if (material.hasSubtype(MaterialUseType.ORE_CHUNK)) {
                    Item ore_chunk = MaterialUseType.ORE_CHUNK.getItem(stone_base.getMaterial(), material);
                    Material processed_material = material;

                    if (material.getProcessedType() != null && material instanceof MaterialMetalBase && !((MaterialMetalBase) material).isNativeMetal()) {
                        processed_material = material.getProcessedType().getMaterial();
                    } else if (material.getProcessedType() != null) {
                        log.info("Material Processed Type not Null put is Native? " + material.getName());
                    }

                    if (processed_material != null) {
                        Item ore_ingot = MaterialUseType.INGOT.getItem(processed_material);

                        if (ore_ingot != null && ore_chunk != null) {
                            addBlastingRecipe(ore_chunk, ore_ingot, 0.15f, 150, consumer);
                            log.debug("Generated Blasting Recipe for: " + ore_chunk.getRegistryName());
                        }
                    }
                    if(processed_material.hasSubtype(MaterialUseType.DIRTY_CRUSHED_ORE)) {
                        crusherBuilder = CrusherRecipeBuilder.builder(tags.dirty_ore_crushed, 1);
                        crusherBuilder.addInput(ore_chunk).setEnergy(6000).build(consumer, toRL("crusher/dirty_crushed_ore_" + material.getName()));
                    } else if(processed_material.hasSubtype(MaterialUseType.DUST))
                    {
                        crusherBuilder = CrusherRecipeBuilder.builder(tags.dust, 1);
                        crusherBuilder.addInput(ore_chunk).setEnergy(6000).build(consumer, toRL("crusher/dust_" + material.getName()));
                    }
                }

                if (material.hasSubtype(MaterialUseType.ORE_BIT)) {
                    Item ore_bit = MaterialUseType.ORE_BIT.getItem(stone_base.getMaterial(), material);
                    Material processed_material = material;

                    if (material.getProcessedType() != null && material instanceof MaterialMetalBase && !((MaterialMetalBase) material).isNativeMetal()) {
                        processed_material = material.getProcessedType().getMaterial();
                    } else if (material.getProcessedType() != null) {
                        //log.info("Material Processed Type not Null put is Native? " + material.getName());
                    }

                    if (processed_material != null) {
                        Item ore_nugget = MaterialUseType.NUGGET.getItem(processed_material);

                        if (ore_nugget != null && ore_bit != null) {
                            addBlastingRecipe(ore_bit, ore_nugget, 0, 50, consumer);
                            log.debug("Generated Blasting Recipe for: " + ore_bit.getRegistryName());
                        }
                    }/* //TODO should ore bits have crushed ore variant?
                    if(processed_material.hasSubtype(MaterialUseType.DIRTY_CRUSHED_ORE)) {
                        crusherBuilder = CrusherRecipeBuilder.builder(tags.dirty_ore_crushed, 1);
                        crusherBuilder.addInput(ore_chunk).setEnergy(6000).build(consumer, toRL("crusher/dirty_crushed_ore_" + material.getName()));
                    } else */ if(processed_material.hasSubtype(MaterialUseType.TINY_DUST))
                    {
                        crusherBuilder = CrusherRecipeBuilder.builder(tags.tiny_dust, 1);
                        crusherBuilder.addInput(ore_bit).setEnergy(600).build(consumer, toRL("crusher/tiny_dust_" + material.getName()));
                    }
                }
            }

            if(material.hasSubtype(MaterialUseType.DIRTY_CRUSHED_ORE) && material.hasSubtype(MaterialUseType.CRUSHED_ORE))
            {
                IGSeparationProcessingMethod separation = new IGSeparationProcessingMethod(120);
                separation.addItemInput(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanilla.getMaterial(), material, MaterialUseType.DIRTY_CRUSHED_ORE)));
                separation.addItemOutput(new ItemStack(IGRegistrationHolder.getItemByMaterial(material, MaterialUseType.CRUSHED_ORE)));
                separation.addItemWaste(new ItemStack(IGRegistrationHolder.getItemByMaterial(MaterialEnum.Vanilla.getMaterial(),
                        MaterialUseType.ROCK_BIT)));
                addSeparationMethod(separation, consumer);
            }
        }

        for (FluidEnum fluidwrap: FluidEnum.values())
        {

            if (fluidwrap.getMaterial() instanceof MaterialSlurryWrapper) {continue;} //slurries registered not here

            IGMaterialProcess processess = fluidwrap.getMaterial().getProcessingMethod();
            if (processess != null) {
                Set<IGProcessingMethod> data = processess.getData();
                for (IGProcessingMethod method : data) {
                    if (method instanceof IGVatProcessingMethod){
                       addVatMethod(method, consumer);
                    }
                }
            }
        }

        for(MaterialEnum wrap : MaterialEnum.values()) {
            /*if(wrap.getMaterial() instanceof MaterialMineralBase) {
                MaterialMineralBase orebase = (MaterialMineralBase) wrap.getMaterial();
                //Gravity Separator

            }
*/
            //Setup Recipe Generation for Mineral Processing
            Material base = wrap.getMaterial();

            //Create Recipe Builders
            IGMaterialProcess processess = base.getProcessingMethod();
            if(processess != null){
                Set<IGProcessingMethod> data = processess.getData();

                for (IGProcessingMethod method : data) {
                    switch (method.getKey()) {
                        case BLASTING:
                            addRedoxMethod(method,consumer);
                            break;
                        case CALCINATION:
                            addCalcinationMethod(method,consumer);
                            break;
                        case CRUSHING:
                            addCrushingMethod(method, consumer);
                            break;
                        case ACID:
                            addVatMethod(method, consumer);
                            break;
                        case BLOOMERY:
                            addBloomeryMethod(method, consumer);
                            break;
                        case ROASTER:
                            addReverberationMethod(method, consumer);
                            break;
                        case ELECTROLYSIS:
                            addCrystalizerMethod(method,consumer);
                            break;
                        case CRAFTING:
                            addCraftingMethod(method, consumer);
                            break;
                        case MOLTEN_REDUCTION:
                            addArcFurnaceMethod(method,consumer);
                            break;
                        case SEPARATING:
                            addSeparationMethod(method, consumer);
                            break;
                    }
                }
            }
        }
        getSubRecipeProviders().forEach(subRecipeProvider -> subRecipeProvider.addRecipes(consumer));
    }

    private void addSeparationMethod(IGProcessingMethod method, Consumer<IFinishedRecipe> consumer) {
        IGSeparationProcessingMethod m = (IGSeparationProcessingMethod) method;
        SeparatorRecipeBuilder builder = SeparatorRecipeBuilder.builder(m.getOutputItem());
        builder.addWaste(m.getWasteItem()).addInput(m.getInputItem()).build(consumer, toRL("gravityseparator/wash_" + m.getOutputItem().getItem().getRegistryName().getPath().toLowerCase()));
        log.info("Registering Washing Recipe");
    }

    private void addArcFurnaceMethod(IGProcessingMethod method, Consumer<IFinishedRecipe> consumer) {
        IGArcFurnaceProcessingMethod m = (IGArcFurnaceProcessingMethod) method;
        ArcFurnaceRecipeBuilder arcFurnaceRecipeBuilder = ArcFurnaceRecipeBuilder.builder(m.getOutputItem());
        if (!m.getSlagItem().isEmpty()) {
            arcFurnaceRecipeBuilder.addSlag(m.getSlagItem());
        }
        arcFurnaceRecipeBuilder.setEnergy(m.getEnergyCost()).setTime(m.getProcessingTime()).addIngredient("input",m.getInputItem());
        for (IngredientWithSize t:m.getAdditives()) {
            arcFurnaceRecipeBuilder.addMultiInput(t);
        }
        arcFurnaceRecipeBuilder.build(consumer, toRL("arcfurnace/"+m.getOutputItem().getItem().getRegistryName().getPath().toLowerCase()));
    }

    private void addCrushingMethod(IGProcessingMethod method, Consumer<IFinishedRecipe> consumer){
        IGCrushingProcessingMethod m = (IGCrushingProcessingMethod) method;

        CrusherRecipeBuilder crusherBuilder = CrusherRecipeBuilder.builder(m.getItemOutput());
        crusherBuilder.addInput(m.getItemInput())
                .setEnergy(m.getEnergyCost())
                .build(consumer, toRL("crusher/" + m.getItemOutput().getDisplayName().getString()));
    }

    private void addRedoxMethod(IGProcessingMethod method,  Consumer<IFinishedRecipe> consumer)
    {
        IGReductionProcessingMethod redoxMethod = (IGReductionProcessingMethod) method;
        int energyCost = redoxMethod.getEnergyCost();
        int processingTime = redoxMethod.getProcessingTime();
        ItemStack outputItem = redoxMethod.getOutputItem();
        ItemStack inputItem = redoxMethod.getInputItem();
        ItemStack slagItem = redoxMethod.getSlagItem();
        BlastFurnaceRecipeBuilder builder = BlastFurnaceRecipeBuilder.builder(outputItem);
        if (slagItem.isEmpty()) {
            builder.addSlag(IETags.slag, 1);
        }
        else{
            builder.addSlag(slagItem);
        }

        builder.addInput(inputItem).setEnergy(energyCost).setTime(processingTime)
                .build(consumer,toRL("ieblastfurnace/" +
                        outputItem.getItem().getRegistryName().getPath().toLowerCase() + "_and_"
                        + slagItem.getItem().getRegistryName().getPath().toLowerCase()));
    }

    private void addCraftingMethod(IGProcessingMethod method, Consumer<IFinishedRecipe> consumer){
        assert method instanceof IGCraftingProcessingMethod;
        IGCraftingProcessingMethod m = (IGCraftingProcessingMethod) method;

        if(m.isShapeless()){
            ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapelessRecipe(m.getOutput().getItem(), m.getOutput().getCount());
            builder.addCriterion(m.getCriterionName(), hasItem(m.getCriterion()));

            for (Item item : m.getShapelessInputs()) {
                builder.addIngredient(item);
            }

            builder.build(consumer, toRL("crafting/" + m.getOutput().getItem().getRegistryName().getPath().toLowerCase()));
            return;
        }

        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shapedRecipe(m.getOutput().getItem(), m.getOutput().getCount());
        builder.addCriterion(m.getCriterionName(), hasItem(m.getCriterion()));

        for (Character k : m.getPatternKeys()) {
            builder.key(k, m.getItemByKey(k));
        }
        builder.patternLine(m.topLine());
        builder.patternLine(m.midLine());
        builder.patternLine(m.botLine());

        builder.build(consumer, toRL("crafting/" + m.getOutput().getItem().getRegistryName().getPath().toLowerCase()));

    }

    private void addReverberationMethod(IGProcessingMethod method, Consumer<IFinishedRecipe> consumer){
        IGRoastingProcessingMethod m = (IGRoastingProcessingMethod) method;
        float wasteMult = m.getWasteMultiplier();
        int timeCost = m.getProcessingTime();

        ItemStack input = m.getInputItem();
        ItemStack output = m.getOutputItem();

        ReverberationRecipeBuilder.builder(output).setTime(timeCost).setWasteMult(wasteMult).addItemInput(input).build(consumer, toRL("reverberation/" + output.getItem().getRegistryName().getPath().toLowerCase()));
    }

    private void addBloomeryMethod(IGProcessingMethod method, Consumer<IFinishedRecipe> consumer) {
        IGBloomeryProcessingMethod m = (IGBloomeryProcessingMethod) method;
        int fuelCost = m.getEnergyCost();
        int timeMult = m.getProcessingTime();

        ItemStack input = m.getInputItem();
        ItemStack output = m.getOutputItem();

        BloomeryRecipeBuilder.builder(output).setTime(timeMult).setEnergy(fuelCost).addItemInput(input).build(consumer, toRL("bloomery/" + output.getItem().getRegistryName().getPath().toLowerCase()));
    }
    private void addCrystalizerMethod (IGProcessingMethod method, Consumer<IFinishedRecipe> consumer)
    {

        IGCrystalizerProcessingMethod crystalMethod = (IGCrystalizerProcessingMethod) method;
        int energyCost = crystalMethod.getEnergyCost();
        int processingTime = crystalMethod.getProcessingTime();
        ItemStack outputItem = crystalMethod.getOutputItem();
        FluidStack input_fluid = crystalMethod.getInputFluid();

        CrystalizerRecipeBuilder crystalizerRecipeBuilder = CrystalizerRecipeBuilder.builder(outputItem)
               .setEnergy(energyCost).setTime(processingTime);
        FluidTagInput input_chemical = new FluidTagInput(FluidTags.WATER, 1);

        if (input_fluid.getFluid() instanceof IGFluid)
        {
            IGFluid igFluid = (IGFluid) input_fluid.getFluid();
            MaterialFluidBase fluidWrapper = igFluid.getFluidMaterial();
            IGTags.MaterialTags tags = IGTags.getTagsFor(fluidWrapper);
            input_chemical = new FluidTagInput(tags.fluid, input_fluid.getAmount());
        }

        crystalizerRecipeBuilder.addFluidInput(input_chemical).build(consumer,toRL("crystalizer/" +
                 outputItem.getItem().getRegistryName().getPath().toLowerCase()));
    }

    private void addCalcinationMethod (IGProcessingMethod method,  Consumer<IFinishedRecipe> consumer)
    {
        IGCalcinationProcessingMethod processingMethod = (IGCalcinationProcessingMethod) method;
        int energyCost = processingMethod.getEnergyCost();
        int processingTime = processingMethod.getProcessingTime();
        ItemStack outputItem = processingMethod.getOutputItem();
        ItemStack inputItem = processingMethod.getInputItem();
        CalcinationRecipeBuilder calcinationRecipeBuilder = CalcinationRecipeBuilder.builder(outputItem)
                .setEnergy(energyCost).setTime(processingTime);
        calcinationRecipeBuilder.addItemInput(inputItem).build(consumer,toRL("rotary_kiln/" +
                outputItem.getItem().getRegistryName().getPath().toLowerCase()));
    }

    private void addVatMethod (IGProcessingMethod method, Consumer<IFinishedRecipe> consumer)
    {
        VatRecipeBuilder vatRecipeBuilder = new VatRecipeBuilder();

        IGVatProcessingMethod m = (IGVatProcessingMethod) method;
        int energyCost = m.getEnergyCost();
        int timeTaken = m.getProcessingTime();

        FluidTagInput primary_chemical = new FluidTagInput(FluidTags.WATER, 1);
        FluidTagInput secondary_chemical = null;

        FluidStack output_chemical = m.getOutputFluid();

        FluidStack primary_input = m.getPrimaryInputFluid();
        if(!primary_input.isEmpty()){
            if(primary_input.getFluid() instanceof IGFluid) {
                IGFluid igFluid = (IGFluid) primary_input.getFluid();
                MaterialFluidBase fluidWrapper = igFluid.getFluidMaterial();
                IGTags.MaterialTags tags = IGTags.getTagsFor(fluidWrapper);
                primary_chemical = new FluidTagInput(tags.fluid, primary_input.getAmount());
            } else {
                primary_chemical = new FluidTagInput(primary_input.getFluid().isEquivalentTo(Fluids.WATER) ? FluidTags.WATER : FluidTags.LAVA, primary_input.getAmount());
            }
        }

        FluidStack secondary_input = m.getSecondaryInputFluid();
        if(!secondary_input.isEmpty()){
            if(secondary_input.getFluid() instanceof IGFluid) {
                IGFluid igFluid = (IGFluid) secondary_input.getFluid();
                MaterialFluidBase fluidWrapper = igFluid.getFluidMaterial();
                IGTags.MaterialTags tags = IGTags.getTagsFor(fluidWrapper);
                secondary_chemical = new FluidTagInput(tags.fluid, secondary_input.getAmount());
            }
        }
        ItemStack outputItem = m.getOutputItem();
        ItemStack itemInput = m.getInputItem() != null ? m.getInputItem() : ItemStack.EMPTY;

        log.info("Registering Chemical Vat Recipe");

        vatRecipeBuilder.builder(outputItem, output_chemical).addItemInput(itemInput).addFluidInputs(primary_chemical, secondary_chemical).setEnergy(energyCost).setTime(timeTaken)
                .build(consumer,toRL("chemicalvat/" + output_chemical.getFluid().getRegistryName().getPath().toLowerCase() +
                        "_" + outputItem.getItem().getRegistryName().getPath().toLowerCase()));

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
                .build(out, toRL(toPath(bigItem)));
        ShapelessRecipeBuilder.shapelessRecipe(smallItem, 9)
                .addIngredient(bigItem)
                .addCriterion("has_"+toPath(bigItem), hasItem(smallItem))
                .build(out, toRL(toPath(smallItem)));
    }
    private void add2x2Combine(IItemProvider bigItem, IItemProvider smallItem, Consumer<IFinishedRecipe> out) {
        ShapedRecipeBuilder.shapedRecipe(bigItem)
                .key('s', smallItem)
                .patternLine("ss")
                .patternLine("ss")
                .addCriterion("has_"+toPath(smallItem), hasItem(smallItem))
                .build(out, toRL(toPath(bigItem)));
    }
    private void add2x2Combine(IItemProvider bigItem, IItemProvider smallItem, ITag.INamedTag<Item> smallTag, Consumer<IFinishedRecipe> out) {
        ShapedRecipeBuilder.shapedRecipe(bigItem)
                .key('s', smallTag)
                .patternLine("ss")
                .patternLine("ss")
                .addCriterion("has_"+toPath(smallItem), hasItem(smallItem))
                .build(out, toRL(toPath(bigItem)));
    }
    private void add3x3Combine(IItemProvider bigItem, IItemProvider smallItem, Consumer<IFinishedRecipe> out) {
        ShapedRecipeBuilder.shapedRecipe(bigItem)
                .key('s', smallItem)
                .patternLine("sss")
                .patternLine("sss")
                .patternLine("sss")
                .addCriterion("has_"+toPath(smallItem), hasItem(smallItem))
                .build(out, toRL(toPath(bigItem)));
    }
    private void add3x3Combine(IItemProvider bigItem, IItemProvider smallItem, ITag.INamedTag<Item> smallTag, Consumer<IFinishedRecipe> out) {
        ShapedRecipeBuilder.shapedRecipe(bigItem)
                .key('s', smallTag)
                .patternLine("sss")
                .patternLine("sss")
                .patternLine("sss")
                .addCriterion("has_"+toPath(smallItem), hasItem(smallItem))
                .build(out, toRL(toPath(bigItem)));
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

    private void addBlastingRecipe(IItemProvider input, IItemProvider output, float xp, int smeltingTime, Consumer<IFinishedRecipe> out) {
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(input), output, xp, smeltingTime).addCriterion("has_"+toPath(input), hasItem(input)).build(out, toRL(toPath(output)+"_blasting"));
    }

    private void addSmeltingRecipe(IItemProvider input, IItemProvider output, float xp, int smeltingTime, Consumer<IFinishedRecipe> out) {
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, xp, smeltingTime).addCriterion("has_"+toPath(input), hasItem(input)).build(out, toRL(toPath(output)+"_from_smelting"));
    }

    private void addCampfireRecipe(IItemProvider input, IItemProvider output, float xp, int smeltingTime, Consumer<IFinishedRecipe> out) {
        CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(input), output, xp, smeltingTime, IRecipeSerializer.CAMPFIRE_COOKING).addCriterion("has_"+toPath(input), hasItem(input)).build(out, toRL(toPath(output)+"_from_campfire_cooking"));
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
