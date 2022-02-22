package com.igteam.immersive_geology.common.block.blocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import blusunrize.immersiveengineering.common.blocks.IETileProviderBlock;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.common.block.tileentity.BloomeryTileEntity;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGTileTypes;
import igteam.immersive_geology.block.IGBlockType;
import igteam.immersive_geology.materials.MetalEnum;
import igteam.immersive_geology.materials.StoneEnum;
import igteam.immersive_geology.materials.helper.IGRegistryProvider;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import igteam.immersive_geology.materials.pattern.MiscPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class BloomeryBlock extends IETileProviderBlock<BloomeryTileEntity> implements IGBlockType {

    protected final IGGenericBlockItem itemBlock;
    public BloomeryBlock() {
        super("bloomery", () -> IGTileTypes.BLOOMERY.get(), Properties.create(Material.ROCK).sound(SoundType.STONE));
        ImmersiveGeology.getNewLogger().info("Setting up Bloomery");

        IEContent.registeredIEBlocks.remove(this);
        Iterator<Item> it = IEContent.registeredIEItems.iterator();
        while(it.hasNext()){
            Item item = it.next();
            if(item instanceof BlockItemIE && ((BlockItemIE) item).getBlock() == this){
                it.remove();
                break;
            }
        }

        IGRegistryProvider.IG_BLOCK_REGISTRY.put(IGRegistrationHolder.getRegistryKey(this), this);

        this.itemBlock = new IGGenericBlockItem(this, MetalEnum.Iron, ItemPattern.block_item);

        IGRegistryProvider.IG_ITEM_REGISTRY.put(IGRegistrationHolder.getRegistryKey(itemBlock), itemBlock);
    }
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    @Override
    public ResourceLocation createRegistryName(){
        return IGRegistryProvider.getRegistryKey(StoneEnum.Stone, MiscPattern.machine);
    }

    @Override
    public Item asItem() {
        return itemBlock;
    }

    @Override
    public boolean canIEBlockBePlaced(BlockState newState, BlockItemUseContext context)
    {
        BlockPos start = context.getPos();
        World w = context.getWorld();
        return areAllReplaceable(start, start.up(), context);
    }

    @Override
    public StateContainer<Block, BlockState> getStateContainer() {
        return super.getStateContainer();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(IEProperties.FACING_HORIZONTAL, IEProperties.MULTIBLOCKSLAVE, LIT);
    }

    @Override
    protected BlockState getInitDefaultState() {
        BlockState state = this.stateContainer.getBaseState();
        return state.with(IEProperties.FACING_HORIZONTAL, getDefaultFacing()).with(LIT, false).getBlockState();
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(LIT)) {
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY();
            double d2 = (double)pos.getZ() + 0.5D;
            if (rand.nextDouble() < 0.1D) {
                worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = stateIn.get(IEProperties.FACING_HORIZONTAL).getOpposite();
            Direction.Axis direction$axis = direction.getAxis();
            double d3 = 0.52D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;
            double d5 = direction$axis == Direction.Axis.X ? (double)direction.getXOffset() * 0.52D : d4;
            double d6 = rand.nextDouble() * 6.0D / 16.0D;
            double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getZOffset() * 0.52D : d4;
            worldIn.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
            worldIn.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public int getColourForIGBlock(int pass) {
        return 0;
    }

    @Override
    public Collection<MaterialInterface> getMaterials() {
        return null;
    }

    @Override
    public MaterialPattern getPattern() {
        return MiscPattern.machine;
    }

    @Override
    public String getHolderKey() {
        StringBuilder data = new StringBuilder();

        data.append("_").append(StoneEnum.Stone.getName());

        return getPattern() + data.toString() + "_" + name;
    }

    @Override
    public Block getBlock() {
        return this;
    }
}
