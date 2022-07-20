package igteam.api.processing.recipe;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import igteam.api.IGApi;
import igteam.api.processing.Serializers;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

public class CrystalRecipe extends IGMultiblockRecipe
{
    public static final IRecipeType<CrystalRecipe> TYPE = IRecipeType.register(IGApi.MODID + ":crystalizer");
    public static Map<ResourceLocation, CrystalRecipe> recipes = new HashMap<>();


    /** May return null! */
    public static CrystalRecipe findRecipe(FluidStack fluidInput)
    {
        IGApi.getNewLogger().warn("Trying for fluid: " + fluidInput.getDisplayName());
        if(!recipes.isEmpty()){
            for(CrystalRecipe r:recipes.values()){
                //IGApi.getNewLogger().warn("Testing against: " + r.fluidInput.getRandomizedExampleStack(0).getDisplayName());
                if(r.fluidInput != null && r.fluidInput.testIgnoringAmount(fluidInput)){
                    return r;
                }
            }
        }
        return null;
    }

    public static CrystalRecipe loadFromNBT(CompoundNBT nbt){
        FluidStack fluidInput = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid_input"));
        return findRecipe(fluidInput);
    }

    protected final FluidTagInput fluidInput;
    protected final ItemStack itemOutput;

    public CrystalRecipe(ResourceLocation id, ItemStack itemOutput, FluidTagInput fluidInput, int energy, int time) {
        super(ItemStack.EMPTY, TYPE, id);
        this.fluidInput = fluidInput;
        this.itemOutput = itemOutput;

        this.fluidInputList = Collections.singletonList(fluidInput);
        this.outputList = NonNullList.from(ItemStack.EMPTY, itemOutput);
        timeAndEnergy(time, energy);
    }

    @Override
    protected IERecipeSerializer getIESerializer() {
        return Serializers.CRYSTALIZER_SERIALIZER.get();
    }

    @Override
    public boolean shouldCheckItemAvailability() {return false;}

    public ItemStack getItemOutput() {
        return itemOutput;
    }

    @Override
    public NonNullList<ItemStack> getItemOutputs() {
        return outputList;
    }

    @Override
    public int getMultipleProcessTicks() {
        return 0;
    }

    public FluidTagInput getInputFluid(){
        return this.fluidInput;
    }

}
