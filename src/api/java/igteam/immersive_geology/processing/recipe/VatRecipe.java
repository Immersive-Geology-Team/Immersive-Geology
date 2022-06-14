package igteam.immersive_geology.processing.recipe;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import igteam.immersive_geology.IGApi;
import igteam.immersive_geology.processing.Serializers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class VatRecipe extends IGMultiblockRecipe
{
    public static final IRecipeType<VatRecipe> TYPE = IRecipeType.register(IGApi.MODID + ":chemicalvat");
    public static Map<ResourceLocation, VatRecipe> recipes = new HashMap<>();

    public static Logger log = IGApi.getNewLogger();

    /** May return null! */
    public static VatRecipe findRecipe(ItemStack itemInput, FluidStack fluid1, FluidStack fluid2)
    {
        if(!recipes.isEmpty()){
            for(VatRecipe r : recipes.values()){

                if(r.itemInput != null && fluid1 != null && fluid2 != null){
                    List<FluidTagInput> recipeFluids = new ArrayList<>();
                    recipeFluids.add(r.fluidInput1);
                    recipeFluids.add(r.fluidInput2);

                    boolean fluid1Found = recipeFluids.stream().anyMatch((recFluid) -> (recFluid.test(fluid1)));
                    boolean fluid2Found = recipeFluids.stream().anyMatch((recFluid) -> (recFluid.test(fluid2)));

                    if(fluid1Found && fluid2Found && r.itemInput.test(itemInput)){
                        return r;
                    }
                }
            }
        }

        return null;
    }

    public static VatRecipe loadFromNBT(CompoundNBT nbt){
        FluidStack fluidInput1 = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid_input1"));
        FluidStack fluidInput2 = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid_input2"));
        ItemStack itemInput =  ItemStack.read(nbt.getCompound("item_input"));
        return findRecipe(itemInput, fluidInput1, fluidInput2);
    }

    protected final FluidTagInput fluidInput1;
    protected final FluidTagInput fluidInput2;

    protected final FluidStack fluidOutput;
    protected final IngredientWithSize itemInput;
    protected final ItemStack itemOutput;

    public VatRecipe(ResourceLocation id, FluidStack fluidOutput, ItemStack itemOutput, FluidTagInput fluidInput1, FluidTagInput fluidInput2, IngredientWithSize itemInput, int energy, int time) {
        super(itemOutput, TYPE, id);
        this.fluidInput1 = fluidInput1;
        this.fluidInput2 = fluidInput2;
        this.fluidOutput = fluidOutput;
        this.itemInput = itemInput;
        this.itemOutput = itemOutput;

        this.fluidInputList = new ArrayList<>();

        if(fluidInput1 != null)
        fluidInputList.add(fluidInput1);

        if(fluidInput2 != null)
        fluidInputList.add(fluidInput2);

        this.fluidOutputList = Arrays.asList(this.fluidOutput);
        this.outputList = NonNullList.from(ItemStack.EMPTY, itemOutput);

        timeAndEnergy(time, energy);
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return Serializers.CHEMICAL_VAT_SERIALIZER.get();
    }

    @Override
    public int getMultipleProcessTicks() {
        return 1;
    }

    @Override
    public List<FluidStack> getActualFluidOutputs(TileEntity tile) {
        NonNullList<FluidStack> output = NonNullList.create();
        output.add(fluidOutput);
        return output;
    }

    @Override
    public List<FluidStack> getFluidOutputs() {
        return fluidOutputList;
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

    public FluidStack getFluidOutput() {
        return fluidOutput;
    }

    public List<FluidTagInput> getInputFluids(){
        return fluidInputList;
    }
}
