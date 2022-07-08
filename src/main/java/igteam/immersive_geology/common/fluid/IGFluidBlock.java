package igteam.immersive_geology.common.fluid;

import igteam.immersive_geology.common.item.helper.IFlaskPickupHandler;
import igteam.api.block.IGBlockType;
import igteam.api.main.IGRegistryProvider;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Supplier;

public class IGFluidBlock extends FlowingFluidBlock implements IGBlockType, IFlaskPickupHandler {
    private final IGFluid igFluid;
    @Nullable
    private Effect effect;
    private int duration;
    private int level;
    private static IGFluid tempFluid;

    private static Supplier<IGFluid> supply(IGFluid fluid){
        tempFluid = fluid;
        return () -> fluid;
    }

    public IGFluidBlock(IGFluid igFluid){
        super(supply(igFluid), Properties.create(Material.WATER));
        this.igFluid = igFluid;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        IGFluid f;
        if(igFluid != null){
            f = igFluid;
        } else {
            f = tempFluid;
        }
        builder.add(f.getStateContainer().getProperties().toArray(new Property[0]));
    }

    @Nonnull
    @Override
    public FluidState getFluidState(BlockState state) {
        FluidState baseState = super.getFluidState(state);
        for(Property<?> prop : igFluid.getStateContainer().getProperties()){
            if(prop != FlowingFluidBlock.LEVEL){
                baseState = withCopiedValue(prop, baseState, state);
            }
        }
        return baseState;
    }

    private <T extends StateHolder<?, T>, S extends Comparable<S>>
    T withCopiedValue(Property<S> prop, T oldState, StateHolder<?, ?> copyFrom)
    {
        return oldState.with(prop, copyFrom.get(prop));
    }

    public void setEffect(@Nonnull Effect effect, int duration, int level)
    {
        this.effect = effect;
        this.duration = duration;
        this.level = level;
    }

    @Override
    public void onEntityCollision(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Entity entityIn)
    {
        super.onEntityCollision(state, worldIn, pos, entityIn);
        if(effect!=null&&entityIn instanceof LivingEntity)
            ((LivingEntity)entityIn).addPotionEffect(new EffectInstance(effect, duration, level));
    }

    @Override
    public Block getSelf() {
        return this;
    }

//    @Override
//    public IFormattableTextComponent getTranslatedName() {
//        ArrayList<String> localizedNames = new ArrayList<>();
//        if(igFluid.fluidMaterial instanceof MaterialSlurryWrapper){
//            MaterialSlurryWrapper slurry = (MaterialSlurryWrapper) igFluid.fluidMaterial;
//            localizedNames.add(slurry.getSoluteMaterial().getDisplayName());
//            localizedNames.add(slurry.getBaseFluidMaterial().getComponentName());
//        } else {
//            localizedNames.add(I18n.format("material." + IGLib.MODID + "." + igFluid.fluidMaterial.getName().toLowerCase()));
//        }
//        TranslationTextComponent name = new TranslationTextComponent("block."+ IGLib.MODID+"."+ MaterialUseType.BUCKET.getName().toLowerCase(Locale.ENGLISH), localizedNames.toArray(new Object[localizedNames.size()]));
//        return name;
//    }

    @Override
    public Fluid pickupFluid(IWorld iWorld, BlockPos pos, BlockState state) {
        return Fluids.EMPTY;
    }

    @Override
    public Fluid pickupFlaskFluid(IWorld iWorld, BlockPos pos, BlockState state) {
        if(igFluid.hasFlask()){
            return super.pickupFluid(iWorld, pos, state);
        } else {
            return Fluids.EMPTY;
        }
    }

    @Override
    public int getColourForIGBlock(int pass) {
        return igFluid.getFluidMaterial().getColor(FluidPattern.fluid);
    }

    @Override
    public Collection<MaterialInterface> getMaterials() {
        return null;
    }

    @Override
    public MaterialPattern getPattern() {
        return igFluid.block.getPattern();
    }

    @Override
    public String getHolderKey() {
        return IGRegistryProvider.getRegistryKey(igFluid.fluidMaterial, getPattern()).getPath();
    }

    @Override
    public Block getBlock() {
        return this;
    }
}

