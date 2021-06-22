package com.igteam.immersive_geology.common.fluid;

import blusunrize.immersiveengineering.common.util.fluids.IEFluid;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.Material;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialFluidBase;
import com.igteam.immersive_geology.common.item.IGBucketItem;
import com.igteam.immersive_geology.common.item.ItemBase;
import com.igteam.immersive_geology.core.lib.IGLib;
import com.igteam.immersive_geology.core.registration.IGRegistration;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
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
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
    protected final MaterialFluidBase fluidMaterial;
    protected IGFluid flowing;
    protected IGFluid source;

    @Nullable
    protected final Consumer<FluidAttributes.Builder> buildAttributes;
    public IGFluidBlock block;
    protected IGBucketItem bucket;
    private int burnTime = -1;

    private boolean isSolid;

    public IGFluid(MaterialFluidBase material)
    {
        this(material, null);
    }

    public IGFluid(MaterialFluidBase material, @Nullable Consumer<FluidAttributes.Builder> buildAttributes)
    {
        this(material, buildAttributes, true);
    }

    public IGFluid(MaterialFluidBase material, @Nullable Consumer<FluidAttributes.Builder> buildAttributes, boolean isSource)
    {
        this.fluidMaterial = material;
        this.fluidName = material.getName();
        this.stillTex = material.getFluidStill();
        this.flowingTex = material.getFluidFlowing();
        this.buildAttributes = buildAttributes;
        this.isSolid = material.getIsSolid();
        if(!isSource){
            flowing = this;
            setRegistryName(IGLib.MODID, IGRegistrationHolder.getRegistryKey(fluidMaterial, MaterialUseType.FLUIDS) + "_flowing");
        }
        else
        {
            source = this;
            this.block = new IGFluidBlock(this);
            this.block.setRegistryName(IGLib.MODID, IGRegistrationHolder.getRegistryKey(fluidMaterial, MaterialUseType.FLUIDS));


            if(fluidMaterial.hasBucket()) {
                this.bucket = new IGBucketItem(() -> this.source, fluidMaterial, MaterialUseType.BUCKET, new Item.Properties().maxStackSize(1).group(ImmersiveGeology.IGGroup).containerItem(Items.BUCKET)) {
                    @Override
                    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
                        return new FluidBucketWrapper(stack);
                    }

                    @Override
                    public int getBurnTime(ItemStack itemStack) {
                        return burnTime;
                    }
                };

                this.bucket.setRegistryName(IGRegistrationHolder.getRegistryKey(fluidMaterial, MaterialUseType.BUCKET));
                DispenserBlock.registerDispenseBehavior(this.bucket, BUCKET_DISPENSE_BEHAVIOR);

            }
            if(fluidMaterial.hasFlask()) {
                Item flask = IGRegistrationHolder.getItemByMaterial(MaterialEnum.Glass.getMaterial(), MaterialUseType.FLASK);
                this.bucket = new IGBucketItem(() -> this.source, fluidMaterial, MaterialUseType.FLASK, new Item.Properties().maxStackSize(1).group(ImmersiveGeology.IGGroup).containerItem(flask)) {
                    @Override
                    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
                        return new FluidBucketWrapper(stack);
                    }

                    @Override
                    public int getBurnTime(ItemStack itemStack) {
                        return burnTime;
                    }
                };

                this.bucket.setRegistryName(IGRegistrationHolder.getRegistryKey(fluidMaterial, MaterialUseType.FLASK));
                DispenserBlock.registerDispenseBehavior(this.bucket, FLASK_DISPENSE_BEHAVIOR);
            }



            flowing = createFlowingVariant();
            setRegistryName(IGRegistrationHolder.getRegistryKey(fluidMaterial, MaterialUseType.FLUIDS));
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
        FluidAttributes.Builder builder = FluidAttributes.builder(stillTex, flowingTex);
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
        IGFluid ret = new IGFluid(fluidMaterial, buildAttributes, false)
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

    public static Consumer<FluidAttributes.Builder> createBuilder(int density, int viscosity)
    {
        return builder -> {
            builder.viscosity(viscosity)
                    .density(density);
        };
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

    public static final IDispenseItemBehavior BUCKET_DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior()
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
                return new ItemStack(Items.BUCKET);
            }
            else
                return this.defaultBehavior.dispense(source, stack);
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
                Item flask = IGRegistrationHolder.getItemByMaterial(MaterialEnum.Glass.getMaterial(), MaterialUseType.FLASK);
                return new ItemStack(flask);
            }
            else
                return this.defaultBehavior.dispense(source, stack);
        }
    };

    public IGBucketItem getBucket() {
        return bucket;
    }

    public boolean hasBucket(){
        return fluidMaterial.hasBucket();
    }

    public boolean hasFlask(){
        return fluidMaterial.hasFlask();
    }

    //controls if the fluid is transparent or not.
    public boolean isSolidFluid() {
        return isSolid;
    }
}
