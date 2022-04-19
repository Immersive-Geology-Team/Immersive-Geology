package com.igteam.immersive_geology.common.fluid;

import com.igteam.immersive_geology.common.fluid.helper.IGFluidAttributes;
import com.igteam.immersive_geology.common.item.distinct.IGBucketItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.materials.MiscEnum;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.data.fluid.MaterialBaseFluid;
import igteam.immersive_geology.materials.data.slurry.variants.MaterialSlurryWrapper;
import igteam.immersive_geology.main.IGRegistryProvider;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import igteam.immersive_geology.menu.helper.IGItemGroup;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class IGFluid extends FlowingFluid {

    public static final Collection<IGFluid> IG_FLUIDS = new ArrayList<>();

    protected final String fluidName;
    protected final ResourceLocation stillTex;
    protected final ResourceLocation flowingTex;
    protected final MaterialBase fluidMaterial;
    protected IGFluid flowing;
    protected IGFluid source;

    @Nullable
    protected final Consumer<IGFluidAttributes.IGBuilder> buildAttributes;
    public IGFluidBlock block;
    protected IGBucketItem bucket;
    private int burnTime = -1;

    private MaterialPattern pattern;

    public IGFluid(MaterialInterface<?> material)
    {
        this(material.get(), null, MiscPattern.fluid);
    }

    public IGFluid(MaterialBase material)
    {
        this(material, null, MiscPattern.fluid);
    }

    public IGFluid(MaterialBase material, @Nullable Consumer<IGFluidAttributes.IGBuilder> buildAttributes, MaterialPattern pattern)
    {
        this(material, buildAttributes, true, pattern);
    }

    public IGFluid(MaterialBase material, @Nullable Consumer<IGFluidAttributes.IGBuilder> buildAttributes, boolean isSource, MaterialPattern pattern)
    {
        this.fluidMaterial = material;
        this.fluidName = material.getName();
        this.stillTex = material.getTextureLocation(MiscPattern.fluid, 0);
        this.flowingTex = material.getTextureLocation(MiscPattern.fluid, 1);
        this.buildAttributes = buildAttributes;
        this.pattern = pattern;

        String registryName = pattern.getName() + "_" + material.getName();

        boolean isSlurry = (pattern == MiscPattern.slurry);

        if(!isSource){
            flowing = this;
            setRegistryName(IGLib.MODID, registryName);
        }
        else
        {
            source = this;
            this.block = new IGFluidBlock(this);
            this.block.setRegistryName(IGLib.MODID, registryName);

            if(isSlurry) {
                MaterialSlurryWrapper slurryFluid = (MaterialSlurryWrapper) fluidMaterial;
                MaterialBaseFluid slurryBase = slurryFluid.getFluidBase().get();
                ResourceLocation containerRegistryName = null;
                if(slurryBase.isFluidPortable(ItemPattern.flask)){
                    this.bucket = new IGBucketItem(() -> this.source, fluidMaterial, ItemPattern.flask, new Item.Properties().maxStackSize(1).group(IGItemGroup.IGGroup).containerItem(MiscEnum.Glass.getItem(ItemPattern.flask))) {
                        @Override
                        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
                            return new FluidBucketWrapper(stack);
                        }

                        @Override
                        public int getBurnTime(ItemStack itemStack) {
                            return burnTime;
                        }
                    };
                    containerRegistryName = IGRegistryProvider.getRegistryKey(slurryFluid, ItemPattern.flask);
                    DispenserBlock.registerDispenseBehavior(this.bucket, FLASK_DISPENSE_BEHAVIOR);
                    this.bucket.setRegistryName(containerRegistryName);
                }
            } else {
                MaterialBaseFluid fluid = (MaterialBaseFluid) fluidMaterial;
                ResourceLocation containerRegistryName = null;
                if(fluid.isFluidPortable(ItemPattern.flask)) {
                    this.bucket = new IGBucketItem(() -> this.source, fluidMaterial, ItemPattern.flask, new Item.Properties().maxStackSize(1).group(IGItemGroup.IGGroup).containerItem(MiscEnum.Glass.getItem(ItemPattern.flask))) {
                        @Override
                        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
                            return new FluidBucketWrapper(stack);
                        }

                        @Override
                        public int getBurnTime(ItemStack itemStack) {
                            return burnTime;
                        }
                    };
                    containerRegistryName = IGRegistryProvider.getRegistryKey(fluid, ItemPattern.flask);

                    DispenserBlock.registerDispenseBehavior(this.bucket, FLASK_DISPENSE_BEHAVIOR);
                    this.bucket.setRegistryName(containerRegistryName);
                }
            }

            flowing = createFlowingVariant();
            setRegistryName(registryName);
            IG_FLUIDS.add(this);
        }
    }


    @OnlyIn(Dist.CLIENT)
    public void addTooltipInfo(FluidStack fluidStack, @Nullable PlayerEntity player, List<ITextComponent> tooltip)
    {
    }



    @Nonnull
    @Override
    public Item getFilledBucket()
    {
        return bucket;
    }

    @Override
    protected boolean canDisplace(FluidState fluidState, IBlockReader blockReader, BlockPos pos, Fluid fluidIn, Direction direction)
    {
        return direction==Direction.DOWN&&!isEquivalentTo(fluidIn);
    }

    @Override
    public boolean isEquivalentTo(Fluid fluidIn)
    {
        return fluidIn==source||fluidIn==flowing;
    }

    //TODO all copied from water. Maybe make configurable?
    @Override
    public int getTickRate(IWorldReader p_205569_1_)
    {
        return 5;
    }

    @Override
    protected float getExplosionResistance()
    {
        return 100;
    }

    @Override
    protected BlockState getBlockState(FluidState state)
    {
        return block.getDefaultState().with(FlowingFluidBlock.LEVEL, getLevelFromState(state));
    }

    @Override
    public boolean isSource(FluidState state)
    {
        return state.getFluid()==source;
    }

    @Override
    public int getLevel(FluidState state)
    {
        if(isSource(state))
            return 8;
        else
            return state.get(LEVEL_1_8);
    }

    @Nonnull
    @Override
    protected FluidAttributes createAttributes()
    {
        IGFluidAttributes.IGBuilder builder = IGFluidAttributes.builder(stillTex, flowingTex);
        if(buildAttributes!=null)
            buildAttributes.accept(builder);
        return builder.build(this);
    }

    @Nonnull
    @Override
    public Fluid getFlowingFluid()
    {
        return flowing;
    }

    @Nonnull
    @Override
    public Fluid getStillFluid()
    {
        return source;
    }

    @Override
    protected boolean canSourcesMultiply()
    {
        return false;
    }

    @Override
    protected void beforeReplacingBlock(IWorld iWorld, BlockPos blockPos, BlockState blockState)
    {

    }

    @Override
    protected int getSlopeFindDistance(IWorldReader iWorldReader)
    {
        return 4;
    }

    @Override
    protected int getLevelDecreasePerBlock(IWorldReader iWorldReader)
    {
        return 1;
    }

    public void setBurnTime(int burnTime)
    {
        this.burnTime = burnTime;
    }


    protected IGFluid createFlowingVariant()
    {
        IGFluid ret = new IGFluid(fluidMaterial, buildAttributes, false, pattern)
        {
            @Override
            protected void fillStateContainer(StateContainer.Builder<Fluid, FluidState> builder)
            {
                super.fillStateContainer(builder);
                builder.add(LEVEL_1_8);
            }
        };
        ret.source = this;
        ret.bucket = bucket;
        ret.block = block;
        ret.setDefaultState(ret.getStateContainer().getBaseState().with(LEVEL_1_8, 7));
        return ret;
    }

    public static Consumer<IGFluidAttributes.IGBuilder> createBuilder(int density, int viscosity, Rarity rarity, int colPass, boolean isGas)
    {
        Color col = new Color(colPass);
        Color finalColor = new Color(col.getRed(), col.getGreen(), col.getBlue(), 255);
        if(isGas){
            return builder -> {
                builder.viscosity(viscosity)
                        .density(density).rarity(rarity).color(finalColor.getRGB()).gaseous();
            };
        } else {
            return builder -> {
                builder.viscosity(viscosity).rarity(rarity)
                        .density(density).color(finalColor.getRGB());
            };
        }
    }

    public static final IDataSerializer<Optional<FluidStack>> OPTIONAL_FLUID_STACK = new IDataSerializer<Optional<FluidStack>>()
    {
        @Override
        public void write(PacketBuffer buf, Optional<FluidStack> value)
        {
            buf.writeBoolean(value.isPresent());
            value.ifPresent(fs -> buf.writeCompoundTag(fs.writeToNBT(new CompoundNBT())));
        }

        @Nonnull
        @Override
        public Optional<FluidStack> read(PacketBuffer buf)
        {
            FluidStack fs = !buf.readBoolean()?null: FluidStack.loadFluidStackFromNBT(buf.readCompoundTag());
            return Optional.ofNullable(fs);
        }

        @Override
        public DataParameter<Optional<FluidStack>> createKey(int id)
        {
            return new DataParameter<>(id, this);
        }

        @Override
        public Optional<FluidStack> copyValue(Optional<FluidStack> value)
        {
            return value.map(FluidStack::copy);
        }
    };

    public static final IDispenseItemBehavior FLASK_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior()
    {
        private final DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();

        public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
        {
            IGBucketItem bucketItem = (IGBucketItem)stack.getItem();
            BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
            World world = source.getWorld();
            if(bucketItem.tryPlaceContainedLiquid(null, world, blockpos, null))
            {
                bucketItem.onLiquidPlaced(world, stack, blockpos);
                Item flask = MiscEnum.Glass.getItem(ItemPattern.flask);
                return new ItemStack(flask);
            }
            else
                return this.defaultBehavior.dispense(source, stack);
        }
    };

    public IGBucketItem getFluidContainer() {
        return bucket;
    }

    //controls if the fluid is transparent or not.
    public boolean isSolidFluid() {
        return false;
    }

    public boolean hasFlask(){
        return fluidMaterial.isFluidPortable(ItemPattern.flask);
    }

    public MaterialBase getFluidMaterial() {
        return fluidMaterial;
    }
}
