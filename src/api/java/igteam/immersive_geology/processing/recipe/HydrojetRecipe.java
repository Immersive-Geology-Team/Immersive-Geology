package igteam.immersive_geology.processing.recipe;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.processing.Serializers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class HydrojetRecipe extends IGMultiblockRecipe
{
    //Used in HydroJetCutterTileEntity
    //Dependent on HYDROJET_CUTTER_SERIALIZER defined in Serializers
    //Same name defined for the TileType in IGTileTypes

    public static final IRecipeType<HydrojetRecipe> TYPE = IRecipeType.register(IGApi.MODID + ":hydrojet_cutter");

    public static Map<ResourceLocation, HydrojetRecipe> recipes = new HashMap<>();

    public static Logger log = IGApi.getNewLogger();

    /** May return null! */
    public static HydrojetRecipe findRecipe(ItemStack itemInput, FluidStack fluidIn)
    {
        return null;
    }

    public static HydrojetRecipe loadFromNBT(CompoundNBT nbt){
        FluidStack fluidInput1 = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluidIn"));
        ItemStack itemInput =  ItemStack.read(nbt.getCompound("item_input"));
        return findRecipe(itemInput, fluidInput1);
    }

    protected final FluidTagInput fluidInput;
    protected final IngredientWithSize itemInput;
    protected final ItemStack itemOutput;

    public HydrojetRecipe(ResourceLocation id, ItemStack itemOutput, FluidTagInput fluidInput1, IngredientWithSize itemInput, int energy, int time) {
        super(itemOutput, TYPE, id);
        this.fluidInput = fluidInput1;
        this.itemInput = itemInput;
        this.itemOutput = itemOutput;

        this.fluidInputList = new ArrayList<>();
        if(fluidInput1 != null)
            fluidInputList.add(fluidInput1);

        this.outputList = NonNullList.from(ItemStack.EMPTY, itemOutput);

        timeAndEnergy(time, energy);
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return Serializers.HYDROJET_CUTTER_SERIALIZER.get();
    }

    @Override
    public int getMultipleProcessTicks() {
        return 1;
    }

    @Override
    public List<IngredientWithSize> getItemInputs() {
        return Arrays.asList(itemInput);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.itemOutput;
    }

    @Override
    public int getTotalProcessTime() {
        return this.totalProcessTime.get();
    }

    @Override
    public int getTotalProcessEnergy() {
        return this.totalProcessEnergy.get();
    }

    @Override
    public NonNullList<ItemStack> getItemOutputs() {
        return outputList;
    }

    public IngredientWithSize getItemInput() {
        return itemInput;
    }

    public ItemStack getItemOutput() {
        return itemOutput;
    }
}
